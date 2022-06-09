package com.lexst64.lingvolivetelegrambot.processors;

import com.lexst64.lingvolivetelegrambot.processors.callbackhandlers.CallbackQueryHandler;
import com.lexst64.lingvolivetelegrambot.processors.callbackhandlers.ContextQueryHandler;
import com.lexst64.lingvolivetelegrambot.processors.callbackhandlers.language.suggesters.SuggestDstLangQueryHandler;
import com.lexst64.lingvolivetelegrambot.processors.callbackhandlers.language.suggesters.SuggestSrcLangQueryHandler;
import com.lexst64.lingvolivetelegrambot.processors.callbackhandlers.language.submitters.SubmitDstLangQueryHandler;
import com.lexst64.lingvolivetelegrambot.processors.callbackhandlers.language.submitters.SubmitSrcLangQueryHandler;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CallbackQueryUpdateProcessor extends BaseUpdateProcessor {

    private final CallbackQueryHandler[] callbackQueryHandlers;

    public CallbackQueryUpdateProcessor(CallbackQueryHandler... callbackQueryHandlers) {
        this.callbackQueryHandlers = callbackQueryHandlers;
    }

    public CallbackQueryUpdateProcessor() {
        this(
                new ContextQueryHandler(),
                new SuggestSrcLangQueryHandler(),
                new SubmitSrcLangQueryHandler(),
                new SuggestDstLangQueryHandler(),
                new SubmitDstLangQueryHandler()
        );
    }

    private void processInvalidQuery(CallbackQuery callbackQuery, AbsSender absSender) {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .text("invalid callback query")
                .build();
        try {
            absSender.execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processUpdate(Update update, AbsSender absSender) {
        CallbackQuery callbackQuery = update.getCallbackQuery();

        for (CallbackQueryHandler handler : callbackQueryHandlers) {
            boolean isProcessed = handler.process(callbackQuery, absSender);
            if (isProcessed) {
                return;
            }
        }

        processInvalidQuery(callbackQuery, absSender);
    }

    @Override
    public boolean verifyUpdate(Update update) {
        return update != null && update.hasCallbackQuery();
    }
}
