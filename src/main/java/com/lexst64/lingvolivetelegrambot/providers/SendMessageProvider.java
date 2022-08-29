package com.lexst64.lingvolivetelegrambot.providers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface SendMessageProvider {

    SendMessage provide(long chatId, long userId, String text);
}
