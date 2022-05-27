package com.lexst64.lingvolivetelegrambot.commands;

import com.lexst64.lingvolivetelegrambot.api.HelpDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class HelpCommand extends BotCommand {
    private final Logger logger;

    public HelpCommand() {
        super("help", "help info");
        logger = LoggerFactory.getLogger(HelpCommand.class);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long chatId = chat.getId();

        logger.debug(String.format("user '%s', command '%s'", user.getUserName(), getCommandIdentifier()));
        try {
            absSender.execute(new SendMessage(chatId.toString(), new HelpDataProvider().provide()));
        } catch (TelegramApiException e) {
            sendError(absSender, chatId, getCommandIdentifier(), user.getUserName());
        }
    }

    void sendError(AbsSender absSender, Long chatId, String commandName, String userName) {
        try {
            absSender.execute(new SendMessage(chatId.toString(), "broken down"));
        } catch (TelegramApiException e) {
            logger.debug(String.format("Error: %s, Command: %s, User: %s", e.getMessage(), commandName, userName));
            e.printStackTrace();
        }
    }
}
