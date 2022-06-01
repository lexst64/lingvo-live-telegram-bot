package com.lexst64.lingvolivetelegrambot.processors;

import com.lexst64.lingvolivetelegrambot.processors.callback.*;
import com.lexst64.lingvolivetelegrambot.processors.callback.language.requesters.DstLangQueryRequester;
import com.lexst64.lingvolivetelegrambot.processors.callback.language.requesters.SrcLangQueryRequester;
import com.lexst64.lingvolivetelegrambot.processors.callback.language.submitters.DstLangQuerySubmitter;
import com.lexst64.lingvolivetelegrambot.processors.callback.language.submitters.SrcLangQuerySubmitter;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class NonCommandUpdateProcessor implements UpdateProcessor {

    private final MessageUpdateProcessor messageUpdateProcessor;
    private final CallbackQueryUpdateProcessor callbackQueryUpdateProcessor;

    public NonCommandUpdateProcessor() {
        messageUpdateProcessor = new MessageUpdateProcessor();
        callbackQueryUpdateProcessor = new CallbackQueryUpdateProcessor(
                new ContextQueryProcessor(),
                new SrcLangQueryRequester(),
                new SrcLangQuerySubmitter(),
                new DstLangQueryRequester(),
                new DstLangQuerySubmitter(),
                new LangQueryProcessor()
        );
    }

    @Override
    public void process(Update update, AbsSender absSender) throws TelegramApiException {
        if (callbackQueryUpdateProcessor.check(update)) {
            callbackQueryUpdateProcessor.process(update, absSender);
        }
        if (messageUpdateProcessor.check(update)) {
            messageUpdateProcessor.process(update, absSender);
        }
    }
}
