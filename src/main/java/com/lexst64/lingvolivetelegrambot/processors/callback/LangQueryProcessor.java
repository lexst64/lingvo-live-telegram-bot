package com.lexst64.lingvolivetelegrambot.processors.callback;

import com.lexst64.lingvolivetelegrambot.processors.callback.language.requesters.DstLangQueryRequester;
import com.lexst64.lingvolivetelegrambot.processors.callback.language.requesters.SrcLangQueryRequester;
import com.lexst64.lingvolivetelegrambot.processors.callback.language.submitters.DstLangQuerySubmitter;
import com.lexst64.lingvolivetelegrambot.processors.callback.language.submitters.SrcLangQuerySubmitter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;

public class LangQueryProcessor implements CallbackQueryProcessor {

    private final String regex;
    private final HashMap<String, CallbackQueryProcessor> executors;

    public LangQueryProcessor() {
        executors = getExecutors(
                new DstLangQueryRequester(),
                new SrcLangQueryRequester(),
                new DstLangQuerySubmitter(),
                new SrcLangQuerySubmitter()
        );
        regex = createCommonRegex();
    }

    private HashMap<String, CallbackQueryProcessor> getExecutors(CallbackQueryProcessor... processors) {
        HashMap<String, CallbackQueryProcessor> executors = new HashMap<>();
        for (CallbackQueryProcessor processor : processors) {
            executors.put(processor.getRegex(), processor);
        }
        return executors;
    }

    private String createCommonRegex() {
        return "^(" + String.join("|", executors.keySet()) + ")$";
    }

    @Override
    public void process(CallbackQuery callbackQuery, AbsSender absSender) throws TelegramApiException {
        String data = callbackQuery.getData();
        for (String regex : executors.keySet()) {
            if (data.matches(regex)) {
                executors.get(regex).process(callbackQuery, absSender);
            }
        }
    }

    @Override
    public String getRegex() {
        return regex;
    }
}
