package com.lexst64.lingvolivetelegrambot.api;

import com.lexst64.lingvoliveapi.LingvoLive;
import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvoliveapi.lang.LangPair;
import com.lexst64.lingvoliveapi.request.GetMinicard;
import com.lexst64.lingvoliveapi.request.GetSuggests;
import com.lexst64.lingvoliveapi.request.GetWordList;
import com.lexst64.lingvoliveapi.response.GetMinicardResponse;
import com.lexst64.lingvoliveapi.response.GetSuggestsResponse;
import com.lexst64.lingvoliveapi.response.GetWordListResponse;
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
        this(System.getenv("TEST_API_KEY"));
    }

    public GetMinicardResponse requestMinicard(long userId, String text) {
        LangPair langPair = dbManager.getLangPair(userId);
        GetMinicard request = new GetMinicard(text, langPair);
        return lingvoLive.execute(request);
    }

    public GetSuggestsResponse requestSuggests(long userId, String text) {
        LangPair langPair = dbManager.getLangPair(userId);
        GetSuggests request = new GetSuggests(text, langPair);
        return lingvoLive.execute(request);
    }

    public GetWordListResponse requestWordList(long userId, String text) {
        LangPair langPair = dbManager.getLangPair(userId);
        GetWordList request = new GetWordList(text, langPair, WORD_LIST_PAGE_SIZE);
        return lingvoLive.execute(request);
    }
}
