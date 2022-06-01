package com.lexst64.lingvolivetelegrambot.processors.callback;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ErrorProcessor implements CallbackQueryProcessor {

    @Override
    public void process(CallbackQuery callbackQuery, AbsSender absSender) throws TelegramApiException {
        process(callbackQuery, absSender, "error occurred while trying to process callback query");
    }

    public void process(CallbackQuery callbackQuery, AbsSender absSender, String message) throws TelegramApiException {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .showAlert(true)
                .text(message)
                .build();
        absSender.execute(answerCallbackQuery);
    }

    @Override
    public String getRegex() {
        return null;
    }
}
