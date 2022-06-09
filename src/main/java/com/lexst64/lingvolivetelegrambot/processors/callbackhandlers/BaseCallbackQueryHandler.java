package com.lexst64.lingvolivetelegrambot.processors.callbackhandlers;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

public abstract class BaseCallbackQueryHandler implements CallbackQueryHandler {

    @Override
    public boolean process(CallbackQuery callbackQuery, AbsSender absSender) {
        if (verifyCallbackQuery(callbackQuery)) {
            processQuery(callbackQuery, absSender);
            return true;
        }
        return false;
    }

    @Override
    public boolean verifyCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        return data != null && data.matches(getRegex()) && callbackQuery.getMessage() != null;
    }

    protected abstract void processQuery(CallbackQuery callbackQuery, AbsSender absSender);
}
