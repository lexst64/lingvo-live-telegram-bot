package com.lexst64.lingvolivetelegrambot.processors.callback.handlers.language.suggesters;

import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvolivetelegrambot.processors.callback.handlers.BaseCallbackQueryHandler;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

abstract class SuggestLangQueryHandler extends BaseCallbackQueryHandler {

    public final static String SPLITTER = ":";

    private EditMessageText createEditMessageText(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        Lang[] languages = getLanguages(callbackQuery.getFrom().getId());
        return EditMessageText.builder()
                .text(getMessageText())
                .messageId(message.getMessageId())
                .chatId(message.getChatId().toString())
                .replyMarkup(createKeyboardMarkup(languages))
                .build();
    }

    private InlineKeyboardMarkup createKeyboardMarkup(Lang[] languages) {
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder markupBuilder = InlineKeyboardMarkup.builder();

        for (Lang lang : languages) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .callbackData(getCallbackData(lang))
                    .text(lang.toString())
                    .build();
            keyboardRow.add(button);
            if (keyboardRow.size() == 3) {
                markupBuilder.keyboardRow(keyboardRow);
                keyboardRow = new ArrayList<>();
            }
        }
        if (keyboardRow.size() > 0) {
            markupBuilder.keyboardRow(keyboardRow);
        }

        return markupBuilder.build();
    }

    @Override
    protected void processQuery(CallbackQuery callbackQuery, AbsSender absSender) {
        try {
            absSender.execute(new AnswerCallbackQuery(callbackQuery.getId()));
            absSender.execute(createEditMessageText(callbackQuery));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    abstract String getMessageText();

    abstract Lang[] getLanguages(long userId);

    abstract String getCallbackData(Lang lang);
}
