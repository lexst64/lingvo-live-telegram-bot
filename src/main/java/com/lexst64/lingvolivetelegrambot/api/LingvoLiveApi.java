package com.lexst64.lingvolivetelegrambot.api;

import com.lexst64.lingvoliveapi.LingvoLive;

public class LingvoLiveApi {

    private static volatile LingvoLive instance;

    private LingvoLiveApi() {

    }

    public static LingvoLive getInstance() {
        LingvoLive result = instance;
        if (result != null) {
            return result;
        }
        synchronized (LingvoLiveApi.class) {
            if (instance == null) {
                instance = new LingvoLive(System.getenv("TEST_API_KEY"));
            }
            return instance;
        }
    }
}
