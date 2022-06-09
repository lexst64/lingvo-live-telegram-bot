package com.lexst64.lingvolivetelegrambot.processors.callbackhandlers;

import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvolivetelegrambot.providers.ContextMessageProvider;
import com.lexst64.lingvolivetelegrambot.database.DBManager;
import com.lexst64.lingvolivetelegrambot.providers.exceptions.ContextsNotFoundException;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ContextQueryHandler extends BaseCallbackQueryHandler {

    public final static String REGEX = "^context:[\\p{L} '`.;]+$";
    public final static String SPLITTER = ":";
    public final static int CALLBACK_TEXT_INDEX = 1;

    private final ContextMessageProvider contextMessageProvider;

    public ContextQueryHandler() {
        contextMessageProvider = new ContextMessageProvider();
    }

    @Override
    public void processQuery(CallbackQuery callbackQuery, AbsSender absSender) {
        answerCallbackQuery(callbackQuery.getId(), absSender);
        sendContextMessage(callbackQuery, absSender);
    }

    private void answerCallbackQuery(String callbackQueryId, AbsSender absSender) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(callbackQueryId);
        try {
            absSender.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendContextMessage(CallbackQuery callbackQuery, AbsSender absSender) {
        long userId = callbackQuery.getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        String callbackText = callbackQuery.getData().split(SPLITTER)[CALLBACK_TEXT_INDEX];

        SendMessage sendMessage;
        try {
            sendMessage = contextMessageProvider.provide(chatId, userId, callbackText);
        } catch (ContextsNotFoundException e) {
            try {
                absSender.execute(new SendMessage(Long.toString(chatId), e.getMessage()));
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
            return;
        }
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getRegex() {
        return REGEX;
    }
}
