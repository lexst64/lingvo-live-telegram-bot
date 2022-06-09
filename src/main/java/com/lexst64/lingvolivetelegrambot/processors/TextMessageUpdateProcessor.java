package com.lexst64.lingvolivetelegrambot.processors;

import com.lexst64.lingvolivetelegrambot.providers.exceptions.SuggestsNotFoundException;
import com.lexst64.lingvolivetelegrambot.providers.exceptions.TranslationNotFoundException;
import com.lexst64.lingvolivetelegrambot.providers.SuggestMessageProvider;
import com.lexst64.lingvolivetelegrambot.providers.TranslationMessageProvider;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TextMessageUpdateProcessor extends BaseUpdateProcessor {

    private final SuggestMessageProvider suggestMessageProvider;
    private final TranslationMessageProvider translationMessageProvider;

    public TextMessageUpdateProcessor() {
        suggestMessageProvider = new SuggestMessageProvider();
        translationMessageProvider = new TranslationMessageProvider();
    }

    @Override
    public void processUpdate(Update update, AbsSender absSender) {
        Message message = update.getMessage();
        long userId = message.getFrom().getId();
        Long chatId = message.getChatId();
        String text = message.getText();

        try {
            sendTranslationMessage(userId, chatId, text, absSender);
        } catch (TranslationNotFoundException e) {
            try {
                sendSuggestsMessage(userId, chatId, text, absSender);
            } catch (SuggestsNotFoundException ex) {
                sendErrorMessage(chatId, absSender);
            }
        }
    }

    @Override
    public boolean verifyUpdate(Update update) {
        return update != null && update.hasMessage() && update.getMessage().hasText();
    }

    private void sendErrorMessage(long chatId, AbsSender absSender) {
        SendMessage sendMessage = new SendMessage(Long.toString(chatId), "can't recognize this word");
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendTranslationMessage(long userId, long chatId, String text, AbsSender absSender) throws TranslationNotFoundException {
        SendMessage sendMessage = translationMessageProvider.provide(chatId, userId, text);
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendSuggestsMessage(long userId, long chatId, String text, AbsSender absSender) throws SuggestsNotFoundException {
        SendMessage sendMessage = suggestMessageProvider.provide(chatId, userId, text);
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
