package com.lexst64.lingvolivetelegrambot.commands;

import com.lexst64.lingvoliveapi.lang.Lang;
import com.lexst64.lingvolivetelegrambot.providers.ContextMessageProvider;
import com.lexst64.lingvolivetelegrambot.database.DBManager;
import com.lexst64.lingvolivetelegrambot.providers.exceptions.ContextsNotFoundException;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ContextCommand extends BotCommand {

    private static final String ARGS_DELIMITER = " ";

    private final ContextMessageProvider contextDataProvider;

    public ContextCommand() {
        super("context", "context for passed word/phrase");
        contextDataProvider = new ContextMessageProvider();
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        long chatId = chat.getId();
        long userId = user.getId();

        if (arguments.length == 0) {
            sendErrorMessage(chatId, "can't find contexts without args", absSender);
            return;
        }

        SendMessage sendMessage;
        try {
            String text = String.join(ARGS_DELIMITER, arguments);
            sendMessage = contextDataProvider.provide(chatId, userId, text);
        } catch (ContextsNotFoundException e) {
            sendErrorMessage(chatId, e.getMessage(), absSender);
            return;
        }

        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendErrorMessage(long chatId, String message, AbsSender absSender) {
        SendMessage sendMessage = new SendMessage(Long.toString(chatId), message);
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
