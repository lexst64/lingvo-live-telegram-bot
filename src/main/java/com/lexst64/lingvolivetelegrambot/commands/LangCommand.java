package com.lexst64.lingvolivetelegrambot.commands;

import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvolivetelegrambot.database.DBManager;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LangCommand extends BotCommand {

    private final DBManager dbManager;

    public LangCommand() {
        super("lang", "change the language pair");
        dbManager = DBManager.getInstance();
    }

    public InlineKeyboardMarkup createKeyboardMarkup(long userId) {
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();

        Lang srcLang = dbManager.getSrcLang(userId);
        Lang dstLang = dbManager.getDstLang(userId);

        Collections.addAll(keyboardRow,
                InlineKeyboardButton.builder()
                        .text(srcLang.toString())
                        .callbackData("srcLang")
                        .build(),
                InlineKeyboardButton.builder()
                        .text(dstLang.toString())
                        .callbackData("dstLang")
                        .build()
        );

        return InlineKeyboardMarkup.builder().keyboardRow(keyboardRow).build();
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage request = new SendMessage(chat.getId().toString(), "Change lang:");
        request.setReplyMarkup(createKeyboardMarkup(user.getId()));
        try {
            absSender.execute(request);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
