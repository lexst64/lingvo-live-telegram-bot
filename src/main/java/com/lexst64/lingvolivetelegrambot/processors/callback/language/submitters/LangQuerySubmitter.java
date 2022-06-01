package com.lexst64.lingvolivetelegrambot.processors.callback.language.submitters;

import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvoliveapi.lang.LangPair;
import com.lexst64.lingvoliveapi.lang.LangType;
import com.lexst64.lingvolivetelegrambot.database.DBManager;
import com.lexst64.lingvolivetelegrambot.processors.callback.CallbackQueryProcessor;
import com.lexst64.lingvolivetelegrambot.processors.callback.ErrorProcessor;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class LangQuerySubmitter implements CallbackQueryProcessor {

    private final ErrorProcessor errorProcessor;
    private final DBManager dbManager;
    private final LangType langType;

    public LangQuerySubmitter() {
        if (this instanceof SrcLangQuerySubmitter) {
            langType = LangType.SRC_LANG;
        } else {
            langType = LangType.DST_LANG;
        }
        errorProcessor = new ErrorProcessor();
        dbManager = DBManager.getInstance();
    }

    /**
     * Checks if the provided lang pair exists
     * */
    private boolean checkLangPair(Lang srcLang, Lang dstLang) {
        for (LangPair pair : LangPair.findPairsBySrcLang(srcLang)) {
            if (pair.getDstLang() == dstLang) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return default destination lang for provided source lang
     * @throws IllegalArgumentException if no destination lang has been found for provided srcLang
     * */
    private Lang getDefaultDstLang(Lang srcLang) {
        LangPair[] langPairs = LangPair.findPairsBySrcLang(srcLang);
        if (langPairs.length > 0) {
            return langPairs[0].getDstLang();
        }
        throw new IllegalArgumentException("lang pair not found by provided src lang");
    }

    private void checkAndUpdateDstLang(long userId) {
        Lang srcLang = dbManager.getSrcLang(userId);
        Lang dstLang = dbManager.getDstLang(userId);
        if (!checkLangPair(srcLang, dstLang)) {
            Lang defaultDstLang;
            try {
                defaultDstLang = getDefaultDstLang(srcLang);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                defaultDstLang = dstLang;
            }
            dbManager.updateDstLang(userId, defaultDstLang);
        }
    }

    private boolean checkCallbackData(String data) {
        return data != null && data.matches(getRegex());
    }

    private String getMessageText() {
        return (langType == LangType.SRC_LANG ? "Source language" : "Destination language") + " has been updated";
    }

    private EditMessageText crateEditMessageText(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        long userId = callbackQuery.getFrom().getId();
        return EditMessageText.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .text(getMessageText())
                .replyMarkup(createKeyboardMarkup(userId))
                .build();
    }

    public InlineKeyboardMarkup createKeyboardMarkup(long userId) {
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();

        Lang srcLang = dbManager.getSrcLang(userId);
        Lang dstLang = dbManager.getDstLang(userId);

        Collections.addAll(keyboardRow,
                InlineKeyboardButton.builder()
                        .text(srcLang.toString())
                        .callbackData("srcLang")
                        .build(),
                InlineKeyboardButton.builder()
                        .text(dstLang.toString())
                        .callbackData("dstLang")
                        .build()
        );

        return InlineKeyboardMarkup.builder().keyboardRow(keyboardRow).build();
    }

    protected void processQuery(CallbackQuery callbackQuery, AbsSender absSender) throws TelegramApiException {
        String callbackData = callbackQuery.getData();
        if (!checkCallbackData(callbackData)) {
            errorProcessor.process(callbackQuery, absSender);
            throw new RuntimeException("incorrect input callback data");
        }

        int langCode = Integer.parseInt(callbackData.split(":")[1]);
        Lang lang = Lang.getLangByCode(langCode);
        if (lang == null) {
            errorProcessor.process(callbackQuery, absSender);
            throw new RuntimeException("Lang not found by code '" + langCode + "'");
        }

        long userId = callbackQuery.getFrom().getId();
        boolean isSuccessful = langType == LangType.SRC_LANG
                ? dbManager.updateSrcLang(userId, lang)
                : dbManager.updateDstLang(userId, lang);
        if (!isSuccessful) {
            errorProcessor.process(callbackQuery, absSender);
            throw new RuntimeException("error occurred while trying to update db");
        }

        if (langType == LangType.SRC_LANG) {
            checkAndUpdateDstLang(userId);
        }

        absSender.execute(crateEditMessageText(callbackQuery));
        absSender.execute(new AnswerCallbackQuery(callbackQuery.getId()));
    }
}
