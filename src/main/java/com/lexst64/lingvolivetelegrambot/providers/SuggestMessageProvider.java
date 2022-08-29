package com.lexst64.lingvolivetelegrambot.providers;

import com.lexst64.lingvoliveapi.response.GetSuggestsResponse;
import com.lexst64.lingvolivetelegrambot.api.LingvoLiveApi;
import com.lexst64.lingvolivetelegrambot.api.exceptions.SuggestsNotFoundException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;

public class SuggestMessageProvider implements SendMessageProvider {

    private final LingvoLiveApi lingvoLiveApi;

    public SuggestMessageProvider() {
        lingvoLiveApi = new LingvoLiveApi();
    }

    private String getSuggests(GetSuggestsResponse res) {
        StringBuilder stringBuilder = new StringBuilder("Maybe you meant:\n");
        Arrays.stream(Arrays.copyOfRange(res.suggests(), 0, 7))
                .forEach(suggest -> stringBuilder.append(suggest).append("\n"));
        return stringBuilder.toString();
    }

    @Override
    public SendMessage provide(long chatId, long userId, String text) {
        try {
            GetSuggestsResponse res = lingvoLiveApi.requestSuggests(userId, text);
            return new SendMessage(Long.toString(chatId), getSuggests(res));
        } catch (SuggestsNotFoundException e) {
            return null;
        }
    }
}
