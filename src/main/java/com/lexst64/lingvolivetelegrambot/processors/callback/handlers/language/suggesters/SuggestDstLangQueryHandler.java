package com.lexst64.lingvolivetelegrambot.processors.callback.handlers.language.suggesters;

import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvoliveapi.lang.LangPair;
import com.lexst64.lingvolivetelegrambot.database.DBManager;

import java.util.Arrays;

public class SuggestDstLangQueryHandler extends SuggestLangQueryHandler {

    public final static String REGEX = "dstLang";

    @Override
    String getCallbackData(Lang lang) {
        return REGEX + SuggestLangQueryHandler.SPLITTER + lang.getCode();
    }

    @Override
    Lang[] getLanguages(long userId) {
        Lang srcLang = DBManager.getInstance().getSrcLang(userId);
        return Arrays.stream(LangPair.findPairsBySrcLang(srcLang))
                .map(LangPair::getDstLang)
                .toArray(Lang[]::new);
    }

    @Override
    String getMessageText() {
        return "Choose the destination language";
    }

    @Override
    public String getRegex() {
        return REGEX;
    }
}
