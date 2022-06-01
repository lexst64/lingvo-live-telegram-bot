package com.lexst64.lingvolivetelegrambot.api;

import com.lexst64.lingvoliveapi.LingvoLive;
import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvoliveapi.request.GetWordList;
import com.lexst64.lingvoliveapi.response.GetWordListResponse;
import com.lexst64.lingvoliveapi.type.WordListItem;

public class ContextDataProvider {

    private String getExamples(GetWordListResponse response) {
        StringBuilder examplesBuilder = new StringBuilder("Contexts for '" + response.prefix() + "'\n\n");
        for (WordListItem item : response.headings()) {
            examplesBuilder.append(item.heading())
                    .append("\n")
                    .append(item.translation())
                    .append("\n\n");
        }
        return examplesBuilder.toString();
    }

    public String provide(String text, Lang srcLang, Lang dstLang) {
        GetWordList request = new GetWordList(text, srcLang, dstLang, 20);
        LingvoLive lingvoLive = LingvoLiveApi.getInstance();

        GetWordListResponse response = lingvoLive.execute(request);
        if (response.isOk()) {
            return getExamples(response);
        }
        return "can't find context for '" + text + "'";
    }
}
