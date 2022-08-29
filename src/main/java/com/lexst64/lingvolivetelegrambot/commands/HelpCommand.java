package com.lexst64.lingvolivetelegrambot.commands;

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
        try {
            String text = """
                    Hi! I'm a translator bot that can translate words and phrases.
                    You can change your language pair using /lang command.
                    Just send any word in source language and I'm going to translate it
                    to destination language

                    Here's my commands:
                    - /lang
                    - /context [word or phrase]
                    - /help
                                        """;
            absSender.execute(new SendMessage(chat.getId().toString(), text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
