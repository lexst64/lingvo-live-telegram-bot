package com.lexst64.lingvolivetelegrambot.processors.callback.handlers.language.submitters;

import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvoliveapi.lang.LangPair;
import com.lexst64.lingvoliveapi.lang.exceptions.LangNotFoundException;
import com.lexst64.lingvolivetelegrambot.database.DBManager;
import com.lexst64.lingvolivetelegrambot.processors.callback.handlers.BaseCallbackQueryHandler;
import com.lexst64.lingvolivetelegrambot.processors.callback.handlers.language.suggesters.SuggestDstLangQueryHandler;
import com.lexst64.lingvolivetelegrambot.processors.callback.handlers.language.suggesters.SuggestSrcLangQueryHandler;
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

abstract class SubmitLangQueryHandler extends BaseCallbackQueryHandler {

    public final static String SPLITTER = ":";
    public final static int CALLBACK_TEXT_INDEX = 1;

    private final DBManager dbManager;

    public SubmitLangQueryHandler() {
        dbManager = DBManager.getInstance();
    }

    private EditMessageText createEditMessageText(CallbackQuery callbackQuery) {
        long userId = callbackQuery.getFrom().getId();
        Message message = callbackQuery.getMessage();

        return EditMessageText.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .text(getMessageText())
                .replyMarkup(createKeyboardMarkup(userId))
                .build();
    }

    private InlineKeyboardButton createKeyboardButton(String buttonText, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(buttonText)
                .callbackData(callbackData)
                .build();
    }

    private InlineKeyboardMarkup createKeyboardMarkup(long userId) {
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();

        LangPair langPair = dbManager.getLangPair(userId);

        Collections.addAll(
                keyboardRow,
                createKeyboardButton(langPair.getSrcLang().toString(), SuggestSrcLangQueryHandler.REGEX),
                createKeyboardButton(langPair.getDstLang().toString(), SuggestDstLangQueryHandler.REGEX)
        );

        return InlineKeyboardMarkup.builder().keyboardRow(keyboardRow).build();
    }

    private Lang retrieveLang(String callbackQueryData) throws LangNotFoundException {
        int langCode = Integer.parseInt(callbackQueryData.split(SPLITTER)[CALLBACK_TEXT_INDEX]);
        return Lang.getLangByCode(langCode);
    }

    private void answerCallbackQueryOnError(String callbackQueryId, AbsSender absSender) {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .showAlert(true)
                .text("Something went wrong. Please, try again")
                .build();
        try {
            absSender.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void processQuery(CallbackQuery callbackQuery, AbsSender absSender) {
        long userId = callbackQuery.getFrom().getId();
        String callbackQueryId = callbackQuery.getId();
        Lang lang;

        try {
            lang = retrieveLang(callbackQuery.getData());
        } catch (LangNotFoundException e) {
            answerCallbackQueryOnError(callbackQueryId, absSender);
            return;
        }

        boolean isUpdated = updateLangPair(userId, lang);
        if (!isUpdated) {
            answerCallbackQueryOnError(callbackQueryId, absSender);
            return;
        }

        try {
            absSender.execute(new AnswerCallbackQuery(callbackQueryId));
            absSender.execute(createEditMessageText(callbackQuery));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    abstract String getMessageText();

    abstract boolean updateLangPair(long userId, Lang lang);
}
