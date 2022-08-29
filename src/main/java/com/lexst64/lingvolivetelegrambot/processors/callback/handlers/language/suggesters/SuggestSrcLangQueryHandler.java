package com.lexst64.lingvolivetelegrambot.processors.callback.handlers.language.suggesters;

import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvoliveapi.lang.LangPair;

public class SuggestSrcLangQueryHandler extends SuggestLangQueryHandler {

    public final static String REGEX = "srcLang";

    @Override
    protected Lang[] getLanguages(long userId) {
        return LangPair.getSrcLangs();
    }

    @Override
    protected String getCallbackData(Lang lang) {
        return REGEX + SuggestLangQueryHandler.SPLITTER + lang.getCode();
    }

    @Override
    String getMessageText() {
        return "Choose the source language";
    }

    @Override
    public String getRegex() {
        return REGEX;
    }
}
