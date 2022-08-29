package com.lexst64.lingvolivetelegrambot.providers;

import com.lexst64.lingvoliveapi.response.GetMinicardResponse;
import com.lexst64.lingvolivetelegrambot.api.LingvoLiveApi;
import com.lexst64.lingvolivetelegrambot.api.exceptions.TranslationNotFoundException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class TranslationMessageProvider implements SendMessageProvider {

    private final LingvoLiveApi lingvoLiveApi;

    public TranslationMessageProvider() {
        lingvoLiveApi = new LingvoLiveApi();
    }

    private InlineKeyboardMarkup createInlineKeyboardMarkup(String text) {
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        keyboardRow.add(
                InlineKeyboardButton.builder()
                        .text("context")
                        .callbackData("context:" + text)
                        .build()
        );
        return InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardRow)
                .build();
    }

    @Override
    public SendMessage provide(long chatId, long userId, String text) {
        try {
            GetMinicardResponse res = lingvoLiveApi.requestTranslation(userId, text);
            return SendMessage.builder()
                    .chatId(Long.toString(chatId))
                    .text(res.translation().translation())
                    .replyMarkup(createInlineKeyboardMarkup(text))
                    .build();
        } catch (TranslationNotFoundException e) {
            return null;
        }
    }
}
