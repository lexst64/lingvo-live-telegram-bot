package com.lexst64.lingvolivetelegrambot.processors.callback.handlers.language.submitters;

import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvoliveapi.lang.LangPair;
import com.lexst64.lingvoliveapi.lang.exceptions.LangPairNotFoundException;
import com.lexst64.lingvolivetelegrambot.database.DBManager;

public class SubmitDstLangQueryHandler extends SubmitLangQueryHandler {

    public final static String REGEX = "dstLang:\\d{4}";
    private final DBManager dbManager;

    public SubmitDstLangQueryHandler() {
        dbManager = DBManager.getInstance();
    }

    /**
     * @return default source language by provided destination language
     * @throws IllegalArgumentException if no source language
     *                                  has been found for provided dstLang
     */
    private Lang getDefaultSrcLang(Lang dstLang) {
        LangPair[] langPairs = LangPair.findPairsByDstLang(dstLang);
        if (langPairs.length > 0) {
            return langPairs[0].getSrcLang();
        }
        throw new IllegalArgumentException("lang pair not found by provided dstLang");
    }

    @Override
    String getMessageText() {
        return "Destination language updated!";
    }

    @Override
    boolean updateLangPair(long userId, Lang dstLang) {
        LangPair langPair;
        try {
            Lang srcLang = dbManager.getSrcLang(userId);
            langPair = LangPair.getPair(srcLang, dstLang);
        } catch (LangPairNotFoundException e) {
            e.printStackTrace();
            langPair = LangPair.getPair(getDefaultSrcLang(dstLang), dstLang);
        }
        return dbManager.updateLangPair(userId, langPair);
    }

    @Override
    public String getRegex() {
        return REGEX;
    }
}
