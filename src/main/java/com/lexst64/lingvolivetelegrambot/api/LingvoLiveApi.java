package com.lexst64.lingvolivetelegrambot.api;

import com.lexst64.lingvoliveapi.LingvoLive;
import com.lexst64.lingvoliveapi.lang.LangPair;
import com.lexst64.lingvoliveapi.request.GetMinicard;
import com.lexst64.lingvoliveapi.request.GetSuggests;
import com.lexst64.lingvoliveapi.request.GetWordList;
import com.lexst64.lingvoliveapi.response.GetMinicardResponse;
import com.lexst64.lingvoliveapi.response.GetSuggestsResponse;
import com.lexst64.lingvoliveapi.response.GetWordListResponse;
import com.lexst64.lingvolivetelegrambot.api.exceptions.ContextsNotFoundException;
import com.lexst64.lingvolivetelegrambot.api.exceptions.SuggestsNotFoundException;
import com.lexst64.lingvolivetelegrambot.api.exceptions.TranslationNotFoundException;
import com.lexst64.lingvolivetelegrambot.database.DBManager;

public class LingvoLiveApi {

    private static final int WORD_LIST_PAGE_SIZE = 20;

    private final LingvoLive lingvoLive;
    private final DBManager dbManager;

    public LingvoLiveApi(String apiKey) {
        lingvoLive = new LingvoLive(apiKey);
        dbManager = DBManager.getInstance();
    }

    public LingvoLiveApi() {
        this(System.getenv("LL_TOKEN"));
    }

    /**
     * @throws TranslationNotFoundException if translation hasn't been found for
     *                                      provided text
     */
    public GetMinicardResponse requestTranslation(long userId, String text) {
        LangPair langPair = dbManager.getLangPair(userId);
        GetMinicardResponse res = lingvoLive.execute(new GetMinicard(text, langPair));
        if (!res.isOk()) {
            throw new TranslationNotFoundException(text);
        }
        return res;
    }

    /**
     * @throws SuggestsNotFoundException if no suggests have been found for
     *                                   provided text
     */
    public GetSuggestsResponse requestSuggests(long userId, String text) {
        LangPair langPair = dbManager.getLangPair(userId);
        GetSuggestsResponse res = lingvoLive.execute(new GetSuggests(text, langPair));
        if (!res.isOk()) {
            throw new SuggestsNotFoundException(text);
        }
        return res;
    }

    /**
     * @throws ContextsNotFoundException if no contexts have been found for
     *                                   provided text
     */
    public GetWordListResponse requestContexts(long userId, String text) {
        LangPair langPair = dbManager.getLangPair(userId);
        GetWordListResponse res = lingvoLive.execute(new GetWordList(text, langPair, WORD_LIST_PAGE_SIZE));
        if (!res.isOk()) {
            throw new ContextsNotFoundException(text);
        }
        return res;
    }
}
