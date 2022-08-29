package com.lexst64.lingvolivetelegrambot.commands;

import com.lexst64.lingvolivetelegrambot.providers.ContextMessageProvider;
import com.lexst64.lingvolivetelegrambot.providers.SendMessageProvider;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ContextCommand extends BotCommand {

    private static final String ARGS_DELIMITER = " ";

    private final SendMessageProvider contextDataProvider;

    public ContextCommand() {
        super("context", "context for passed word/phrase");
        contextDataProvider = new ContextMessageProvider();
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        try {
            SendMessage sendMessage;
            if (arguments.length == 0) {
                sendMessage = new SendMessage(chat.getId().toString(), "can't find contexts without args");
            } else {
                String text = String.join(ARGS_DELIMITER, arguments);
                sendMessage = contextDataProvider.provide(chat.getId(), user.getId(), text);
                if (sendMessage == null) {
                    sendMessage = new SendMessage(chat.getId().toString(), "contexts not found for '" + text + "'");
                }
            }
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
