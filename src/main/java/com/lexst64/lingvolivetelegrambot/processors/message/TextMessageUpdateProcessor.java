package com.lexst64.lingvolivetelegrambot.processors.message;

import com.lexst64.lingvolivetelegrambot.processors.BaseNonCommandUpdateProcessor;
import com.lexst64.lingvolivetelegrambot.providers.SendMessageProvider;
import com.lexst64.lingvolivetelegrambot.providers.SuggestMessageProvider;
import com.lexst64.lingvolivetelegrambot.providers.TranslationMessageProvider;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TextMessageUpdateProcessor extends BaseNonCommandUpdateProcessor {

    private final SendMessageProvider suggestMessageProvider;
    private final SendMessageProvider translationMessageProvider;

    public TextMessageUpdateProcessor() {
        suggestMessageProvider = new SuggestMessageProvider();
        translationMessageProvider = new TranslationMessageProvider();
    }

    @Override
    public void processUpdate(Update update, AbsSender absSender) {
        Message message = update.getMessage();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        String text = message.getText();

        SendMessage sendMessage = translationMessageProvider.provide(chatId, userId, text);

        if (sendMessage == null) {
            sendMessage = suggestMessageProvider.provide(chatId, userId, text);
            if (sendMessage == null) {
                sendMessage = new SendMessage(Long.toString(chatId), "can't recognise this word");
            }
        }

        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean verifyUpdate(Update update) {
        return update != null && update.hasMessage() && update.getMessage().hasText();
    }
}
