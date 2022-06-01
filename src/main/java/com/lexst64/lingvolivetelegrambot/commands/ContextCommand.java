package com.lexst64.lingvolivetelegrambot.commands;

import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvolivetelegrambot.api.ContextDataProvider;
import com.lexst64.lingvolivetelegrambot.database.DBManager;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ContextCommand extends BotCommand {

    private final ContextDataProvider dataProvider;
    private final DBManager dbManager;

    public ContextCommand() {
        super("context", "context for passed word/phrase");
        dataProvider = new ContextDataProvider();
        dbManager = DBManager.getInstance();
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        String chatId = chat.getId().toString();

        if (arguments.length == 0) {
            try {
                absSender.execute(new SendMessage(chatId, "can't process command without args"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }

        String text = String.join(" ", arguments);

        long userId = user.getId();
        Lang srcLang = dbManager.getSrcLang(userId);
        Lang dstLang = dbManager.getDstLang(userId);

        String data = dataProvider.provide(text, srcLang, dstLang);
        try {
            absSender.execute(new SendMessage(chat.getId().toString(), data));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
