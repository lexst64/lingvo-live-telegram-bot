package com.lexst64.lingvolivetelegrambot.processors;

import com.lexst64.lingvoliveapi.LingvoLive;
import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvoliveapi.request.GetMinicard;
import com.lexst64.lingvoliveapi.request.GetSuggests;
import com.lexst64.lingvoliveapi.response.GetMinicardResponse;
import com.lexst64.lingvoliveapi.response.GetSuggestsResponse;
import com.lexst64.lingvolivetelegrambot.api.LingvoLiveApi;
import com.lexst64.lingvolivetelegrambot.database.DBManager;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class MessageUpdateProcessor implements UpdateProcessor {

    private final LingvoLive lingvoLive;
    private final DBManager dbManager;

    public MessageUpdateProcessor() {
        lingvoLive = LingvoLiveApi.getInstance();
        dbManager = DBManager.getInstance();
    }

    private InlineKeyboardMarkup createInlineKeyboardMarkup(String text) {
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        keyboardRow.add(InlineKeyboardButton.builder().text("context").callbackData("context:" + text).build());
        return InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardRow)
                .build();
    }

    private GetMinicardResponse requestMinicard(long userId, String text) {
        Lang srcLang = dbManager.getSrcLang(userId);
        Lang dstLang = dbManager.getDstLang(userId);
        GetMinicard request = new GetMinicard(text, srcLang, dstLang);
        return lingvoLive.execute(request);
    }

    private GetSuggestsResponse requestSuggests(long userId, String text) {
        Lang srcLang = dbManager.getSrcLang(userId);
        Lang dstLang = dbManager.getDstLang(userId);
        GetSuggests request = new GetSuggests(text, srcLang, dstLang);
        return lingvoLive.execute(request);
    }

    public boolean check(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    @Override
    public void process(Update update, AbsSender absSender) throws TelegramApiException {
        Long userId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText();

        GetMinicardResponse minicardResponse = requestMinicard(userId, text);
        if (minicardResponse.isOk()) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(userId.toString())
                    .text(minicardResponse.translation().translation())
                    .replyMarkup(createInlineKeyboardMarkup(text))
                    .build();
            absSender.execute(sendMessage);
            return;
        }
        GetSuggestsResponse suggestsResponse = requestSuggests(userId, text);
        if (suggestsResponse.isOk()) {
            StringBuilder suggestMessageBuilder = new StringBuilder("Maybe you meant:\n");
            for (String suggest : suggestsResponse.suggests()) {
                suggestMessageBuilder.append(suggest).append("\n");
            }
            SendMessage sendMessage = new SendMessage(userId.toString(), suggestMessageBuilder.toString());
            absSender.execute(sendMessage);
            return;
        }
        SendMessage sendMessage = new SendMessage(userId.toString(), "can't recognize this word");
        absSender.execute(sendMessage);
    }
}
