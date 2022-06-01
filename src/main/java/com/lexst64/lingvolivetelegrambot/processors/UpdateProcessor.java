package com.lexst64.lingvolivetelegrambot.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface UpdateProcessor {

    void process(Update update, AbsSender absSender) throws TelegramApiException;
}
