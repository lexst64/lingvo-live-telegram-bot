package com.lexst64.lingvolivetelegrambot.processors.callback.language.requesters;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SrcLangQueryRequester extends LangQueryRequester {

    public final static String REGEX = "srcLang";

    @Override
    public void process(CallbackQuery callbackQuery, AbsSender absSender) throws TelegramApiException {
        processQuery(callbackQuery, absSender);
    }

    @Override
    public String getRegex() {
        return REGEX;
    }
}
