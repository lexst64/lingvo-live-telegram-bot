package com.lexst64.lingvolivetelegrambot.processors.callback;

import com.lexst64.lingvolivetelegrambot.processors.BaseNonCommandUpdateProcessor;
import com.lexst64.lingvolivetelegrambot.processors.callback.handlers.CallbackQueryHandler;
import com.lexst64.lingvolivetelegrambot.processors.callback.handlers.context.ContextQueryHandler;
import com.lexst64.lingvolivetelegrambot.processors.callback.handlers.language.submitters.SubmitDstLangQueryHandler;
import com.lexst64.lingvolivetelegrambot.processors.callback.handlers.language.submitters.SubmitSrcLangQueryHandler;
import com.lexst64.lingvolivetelegrambot.processors.callback.handlers.language.suggesters.SuggestDstLangQueryHandler;
import com.lexst64.lingvolivetelegrambot.processors.callback.handlers.language.suggesters.SuggestSrcLangQueryHandler;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CallbackQueryUpdateProcessor extends BaseNonCommandUpdateProcessor {

    private final CallbackQueryHandler[] callbackQueryHandlers;

    public CallbackQueryUpdateProcessor() {
        this.callbackQueryHandlers = new CallbackQueryHandler[]{
                new ContextQueryHandler(),
                new SuggestSrcLangQueryHandler(),
                new SubmitSrcLangQueryHandler(),
                new SuggestDstLangQueryHandler(),
                new SubmitDstLangQueryHandler()
        };
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
