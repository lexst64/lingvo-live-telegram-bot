package com.lexst64.lingvolivetelegrambot.processors.callback.language.submitters;

import com.lexst64.lingvolivetelegrambot.processors.callback.CallbackQueryProcessor;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DstLangQuerySubmitter extends LangQuerySubmitter implements CallbackQueryProcessor {

    public final static String REGEX = "dstLang:\\d{4}";

    @Override
    public void process(CallbackQuery callbackQuery, AbsSender absSender) throws TelegramApiException {
        processQuery(callbackQuery, absSender);
    }

    @Override
    public String getRegex() {
        return REGEX;
    }
}
