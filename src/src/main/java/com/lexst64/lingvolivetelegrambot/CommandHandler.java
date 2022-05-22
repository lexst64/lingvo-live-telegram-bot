package com.lexst64.lingvolivetelegrambot;

import com.pengrad.telegrambot.model.Update;

@FunctionalInterface
public interface CommandHandler {
    void process(Update update);
}
