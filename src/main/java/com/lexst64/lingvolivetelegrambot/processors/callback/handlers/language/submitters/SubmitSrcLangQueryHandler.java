package com.lexst64.lingvolivetelegrambot.processors.callback.handlers.language.submitters;

import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvoliveapi.lang.LangPair;
import com.lexst64.lingvoliveapi.lang.exceptions.LangPairNotFoundException;
import com.lexst64.lingvolivetelegrambot.database.DBManager;

public class SubmitSrcLangQueryHandler extends SubmitLangQueryHandler {

    public final static String REGEX = "srcLang:\\d{4}";
    private final DBManager dbManager;

    public SubmitSrcLangQueryHandler() {
        dbManager = DBManager.getInstance();
    }

    /**
     * @return default destination language by provided source language
     * @throws IllegalArgumentException if no destination language
     *                                  has been found for provided srcLang
     */
    private Lang getDefaultDstLang(Lang srcLang) {
        LangPair[] langPairs = LangPair.findPairsBySrcLang(srcLang);
        if (langPairs.length > 0) {
            return langPairs[0].getDstLang();
        }
        throw new IllegalArgumentException("lang pair not found by provided srcLang");
    }

    @Override
    boolean updateLangPair(long userId, Lang srcLang) {
        LangPair langPair;
        try {
            Lang dstLang = dbManager.getDstLang(userId);
            langPair = LangPair.getPair(srcLang, dstLang);
        } catch (LangPairNotFoundException e) {
            langPair = LangPair.getPair(srcLang, getDefaultDstLang(srcLang));
        }
        return dbManager.updateLangPair(userId, langPair);
    }

    @Override
    String getMessageText() {
        return "Source language updated!";
    }

    @Override
    public String getRegex() {
        return REGEX;
    }
}
