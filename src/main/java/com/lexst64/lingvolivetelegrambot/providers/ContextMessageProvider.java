package com.lexst64.lingvolivetelegrambot.providers;

import com.lexst64.lingvoliveapi.response.GetWordListResponse;
import com.lexst64.lingvoliveapi.type.WordListItem;
import com.lexst64.lingvolivetelegrambot.api.LingvoLiveApi;
import com.lexst64.lingvolivetelegrambot.api.exceptions.ContextsNotFoundException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class ContextMessageProvider implements SendMessageProvider {

    private static final String INDENT = "\n\n";
    private static final String LF = "\n";

    private final LingvoLiveApi lingvoLiveApi;

    public ContextMessageProvider() {
        lingvoLiveApi = new LingvoLiveApi();
    }

    private String getContexts(GetWordListResponse response) {
        StringBuilder stringBuilder = new StringBuilder("Contexts for '" + response.prefix() + "':" + INDENT);
        for (WordListItem item : response.headings()) {
            stringBuilder.append(item.heading())
                    .append(LF)
                    .append(item.translation())
                    .append(INDENT);
        }
        return stringBuilder.toString();
    }

    @Override
    public SendMessage provide(long chatId, long userId, String text) {
        try {
            GetWordListResponse res = lingvoLiveApi.requestContexts(userId, text);
            return new SendMessage(Long.toString(chatId), getContexts(res));
        } catch (ContextsNotFoundException e) {
            return null;
        }
    }
}
