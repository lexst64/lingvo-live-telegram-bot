package com.lexst64.lingvolivetelegrambot.processors.callback;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface CallbackQueryProcessor {

    void process(CallbackQuery callbackQuery, AbsSender absSender) throws TelegramApiException;

    String getRegex();
}
