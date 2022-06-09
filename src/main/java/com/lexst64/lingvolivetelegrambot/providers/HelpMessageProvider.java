package com.lexst64.lingvolivetelegrambot.providers;

import com.lexst64.lingvolivetelegrambot.commands.ContextCommand;
import com.lexst64.lingvolivetelegrambot.commands.HelpCommand;
import com.lexst64.lingvolivetelegrambot.commands.LangCommand;
import com.lexst64.lingvolivetelegrambot.commands.StartCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class HelpMessageProvider {

    private static final String LF = "\n";
    private static final String INDENT = "\n\n";
    private static final String DASH = " - ";

    private final BotCommand[] botCommands;

    public HelpMessageProvider() {
        botCommands = new BotCommand[]{
                new ContextCommand(),
                new HelpCommand(),
                new LangCommand(),
                new StartCommand()
        };
    }

    private String getCommandInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        for (BotCommand botCommand : botCommands) {
            stringBuilder.append(BotCommand.COMMAND_INIT_CHARACTER)
                    .append(botCommand.getCommandIdentifier())
                    .append(DASH)
                    .append(botCommand.getDescription())
                    .append(LF);
        }
        return stringBuilder.toString();
    }

    private String getHeader() {
        return "Hi! I'm a translator bot that can translate words and phrases. "
                + "You can change your language pair using /lang command.";
    }

    public SendMessage provide(long chatId) {
        String text = getHeader() + INDENT + getCommandInfo();
        return new SendMessage(Long.toString(chatId), text);
    }
}
