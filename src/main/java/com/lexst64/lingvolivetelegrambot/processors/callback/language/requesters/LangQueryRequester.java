package com.lexst64.lingvolivetelegrambot.processors.callback.language.requesters;

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
import java.util.Arrays;
import java.util.List;

public abstract class LangQueryRequester implements CallbackQueryProcessor {

    private final DBManager dbManager;
    private final LangType langType;

    LangQueryRequester() {
        if (this instanceof SrcLangQueryRequester) {
            this.langType = LangType.SRC_LANG;
        } else {
            this.langType = LangType.DST_LANG;
        }
        dbManager = DBManager.getInstance();
    }

    protected void processQuery(CallbackQuery callbackQuery, AbsSender absSender) throws TelegramApiException {
        absSender.execute(crateEditMessageText(callbackQuery));
        absSender.execute(new AnswerCallbackQuery(callbackQuery.getId()));
    }

    private EditMessageText crateEditMessageText(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        Lang[] languages = getLanguages(callbackQuery.getFrom().getId());
        return EditMessageText.builder()
                .text(getMessageText())
                .messageId(message.getMessageId())
                .chatId(message.getChatId().toString())
                .replyMarkup(createKeyboardMarkup(languages))
                .build();
    }

    private String getMessageText() {
        return "Choose the " + (langType == LangType.SRC_LANG ? "source language" : "destination language");
    }

    private String getCallbackData(Lang lang) {
        return langType == LangType.SRC_LANG ? "srcLang:" + lang.getCode() : "dstLang:" + lang.getCode();
    }

    private Lang[] getLanguages(long userId) {
        if (langType == LangType.SRC_LANG) {
            return LangPair.getSrcLangs();
        }
        Lang srcLang = dbManager.getSrcLang(userId);
        return Arrays.stream(LangPair.findPairsBySrcLang(srcLang)).map(LangPair::getDstLang).toArray(Lang[]::new);
    }

    private InlineKeyboardMarkup createKeyboardMarkup(Lang[] languages) {
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder markupBuilder = InlineKeyboardMarkup.builder();

        for (Lang lang : languages) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .callbackData(getCallbackData(lang))
                    .text(lang.toString())
                    .build();
            keyboardRow.add(button);
            if (keyboardRow.size() == 3) {
                markupBuilder.keyboardRow(keyboardRow);
                keyboardRow = new ArrayList<>();
            }
        }
        if (keyboardRow.size() > 0) {
            markupBuilder.keyboardRow(keyboardRow);
        }

        return markupBuilder.build();
    }
}
