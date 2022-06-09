package com.lexst64.lingvolivetelegrambot.processors.callbackhandlers.language.suggesters;


public class SuggestDstLangQueryHandler extends SuggestLangQueryHandler {

    public final static String REGEX = "dstLang";

    @Override
    public String getRegex() {
        return REGEX;
    }
}
