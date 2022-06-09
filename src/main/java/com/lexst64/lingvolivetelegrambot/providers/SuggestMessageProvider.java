package com.lexst64.lingvolivetelegrambot.providers;

import com.lexst64.lingvoliveapi.response.GetSuggestsResponse;
import com.lexst64.lingvolivetelegrambot.api.LingvoLiveApi;
import com.lexst64.lingvolivetelegrambot.providers.exceptions.SuggestsNotFoundException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class SuggestMessageProvider {

    private final LingvoLiveApi lingvoLiveApi;

    public SuggestMessageProvider() {
        lingvoLiveApi = new LingvoLiveApi();
    }

    public SendMessage provide(long chatId, long userId, String text) throws SuggestsNotFoundException {
        GetSuggestsResponse suggestsResponse = lingvoLiveApi.requestSuggests(userId, text);
        if (suggestsResponse.isOk()) {
            StringBuilder stringBuilder = new StringBuilder("Maybe you meant:\n");
            for (String suggest : suggestsResponse.suggests()) {
                stringBuilder.append(suggest).append("\n");
            }
            return new SendMessage(Long.toString(chatId), stringBuilder.toString());
        }
        throw new SuggestsNotFoundException(text);
    }
}
