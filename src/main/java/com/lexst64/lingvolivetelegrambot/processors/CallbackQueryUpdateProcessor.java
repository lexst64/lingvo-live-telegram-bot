package com.lexst64.lingvolivetelegrambot.processors;

import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvoliveapi.lang.LangPair;
import com.lexst64.lingvolivetelegrambot.api.ContextDataProvider;
import com.lexst64.lingvolivetelegrambot.database.DBManager;
import com.lexst64.lingvolivetelegrambot.processors.UpdateProcessor;
import com.lexst64.lingvolivetelegrambot.processors.callback.CallbackQueryProcessor;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.*;

public class CallbackQueryUpdateProcessor implements UpdateProcessor {

    private final CallbackQueryProcessor[] callbackQueryProcessors;

    public CallbackQueryUpdateProcessor(CallbackQueryProcessor... callbackQueryProcessors) {
        this.callbackQueryProcessors = callbackQueryProcessors;
    }

    private void processInvalidQuery(CallbackQuery callbackQuery, AbsSender absSender) throws TelegramApiException {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .text("invalid callback query")
                .build();
        absSender.execute(answerCallbackQuery);
    }

    public boolean check(Update update) {
        return update.hasCallbackQuery();
    }

    @Override
    public void process(Update update, AbsSender absSender) throws TelegramApiException {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();

        for (CallbackQueryProcessor processor : callbackQueryProcessors) {
            if (data.matches(processor.getRegex())) {
                processor.process(callbackQuery, absSender);
                return;
            }
        }

        processInvalidQuery(callbackQuery, absSender);
    }
}
