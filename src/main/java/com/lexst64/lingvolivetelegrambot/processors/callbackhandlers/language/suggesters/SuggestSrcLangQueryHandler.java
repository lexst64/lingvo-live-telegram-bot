package com.lexst64.lingvolivetelegrambot.processors.callbackhandlers.language.suggesters;


public class SuggestSrcLangQueryHandler extends SuggestLangQueryHandler {

    public final static String REGEX = "srcLang";

    @Override
    public String getRegex() {
        return REGEX;
    }
}
