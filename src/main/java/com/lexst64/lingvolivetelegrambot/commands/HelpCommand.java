package com.lexst64.lingvolivetelegrambot.commands;

import com.lexst64.lingvolivetelegrambot.providers.HelpMessageProvider;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class HelpCommand extends BotCommand {

    public HelpCommand() {
        super("help", "help info");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        long chatId = chat.getId();

        try {
            absSender.execute(new HelpMessageProvider().provide(chatId));
        } catch (TelegramApiException e) {
            sendErrorMessage(absSender, chatId);
        }
    }

    void sendErrorMessage(AbsSender absSender, Long chatId) {
        try {
            absSender.execute(new SendMessage(chatId.toString(), "broken down"));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
