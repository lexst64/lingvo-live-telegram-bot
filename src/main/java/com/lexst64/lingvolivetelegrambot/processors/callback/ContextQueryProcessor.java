package com.lexst64.lingvolivetelegrambot.processors.callback;

import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvolivetelegrambot.api.ContextDataProvider;
import com.lexst64.lingvolivetelegrambot.database.DBManager;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ContextQueryProcessor implements CallbackQueryProcessor {

    public final static String REGEX = "^context:[\\p{L} '`.;]+$";

    private final DBManager dbManager;
    private final ErrorProcessor errorProcessor;
    private final ContextDataProvider contextDataProvider;


    public ContextQueryProcessor() {
        dbManager = DBManager.getInstance();
        errorProcessor = new ErrorProcessor();
        contextDataProvider = new ContextDataProvider();
    }

    @Override
    public void process(CallbackQuery callbackQuery, AbsSender absSender) throws TelegramApiException {
        AnswerCallbackQuery.AnswerCallbackQueryBuilder answerCallbackQueryBuilder = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId());

        Lang srcLang = dbManager.getSrcLang(callbackQuery.getFrom().getId());
        Lang dstLang = dbManager.getDstLang(callbackQuery.getFrom().getId());

        if (srcLang == null || dstLang == null) {
            errorProcessor.process(callbackQuery, absSender);
            return;
        }

        String callbackText = callbackQuery.getData().split(":")[1];
        AnswerCallbackQuery answerCallbackQuery = answerCallbackQueryBuilder.text("done").build();
        absSender.execute(answerCallbackQuery);
        SendMessage sendMessage = SendMessage.builder()
                .text(contextDataProvider.provide(callbackText, srcLang, dstLang))
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .build();
        absSender.execute(sendMessage);
    }

    @Override
    public String getRegex() {
        return REGEX;
    }
}
