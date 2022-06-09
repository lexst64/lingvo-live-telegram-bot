package com.lexst64.lingvolivetelegrambot.providers;

import com.lexst64.lingvoliveapi.response.GetMinicardResponse;
import com.lexst64.lingvolivetelegrambot.api.LingvoLiveApi;
import com.lexst64.lingvolivetelegrambot.providers.exceptions.TranslationNotFoundException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class TranslationMessageProvider {

    private final LingvoLiveApi lingvoLiveApi;

    public TranslationMessageProvider() {
        lingvoLiveApi = new LingvoLiveApi();
    }

    public SendMessage provide(long chatId, long userId, String text) throws TranslationNotFoundException {
        GetMinicardResponse minicardResponse = lingvoLiveApi.requestMinicard(userId, text);
        if (minicardResponse.isOk()) {
            return SendMessage.builder()
                    .chatId(Long.toString(chatId))
                    .text(minicardResponse.translation().translation())
                    .replyMarkup(createInlineKeyboardMarkup(text))
                    .build();
        }
        throw new TranslationNotFoundException(text);
    }

    private InlineKeyboardMarkup createInlineKeyboardMarkup(String text) {
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        keyboardRow.add(InlineKeyboardButton.builder().text("context").callbackData("context:" + text).build());
        return InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardRow)
                .build();
    }
}
