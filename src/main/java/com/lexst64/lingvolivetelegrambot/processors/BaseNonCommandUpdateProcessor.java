package com.lexst64.lingvolivetelegrambot.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

public abstract class BaseNonCommandUpdateProcessor implements NonCommandUpdateProcessor {

    @Override
    public boolean process(Update update, AbsSender absSender) {
        if (verifyUpdate(update)) {
            processUpdate(update, absSender);
            return true;
        }
        return false;
    }

    protected abstract void processUpdate(Update update, AbsSender absSender);
}
