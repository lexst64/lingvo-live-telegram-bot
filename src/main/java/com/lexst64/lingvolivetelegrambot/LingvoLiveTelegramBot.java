package com.lexst64.lingvolivetelegrambot;

import com.lexst64.lingvolivetelegrambot.commands.ContextCommand;
import com.lexst64.lingvolivetelegrambot.commands.HelpCommand;
import com.lexst64.lingvolivetelegrambot.commands.LangCommand;
import com.lexst64.lingvolivetelegrambot.commands.StartCommand;
import com.lexst64.lingvolivetelegrambot.processors.NonCommandUpdateProcessor;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LingvoLiveTelegramBot extends TelegramLongPollingCommandBot {

    private final static String BOT_USERNAME = "LingvoLiveBot";

    private final String botToken;
    private final NonCommandUpdateProcessor nonCommandUpdateProcessor;
    private final List<IBotCommand> commands;

    public LingvoLiveTelegramBot(String botToken) {
        super();
        this.botToken = botToken;
        nonCommandUpdateProcessor = new NonCommandUpdateProcessor();
        commands = new ArrayList<>();
        Collections.addAll(commands, new HelpCommand(), new ContextCommand(), new StartCommand(), new LangCommand());
        registerAll(commands.toArray(IBotCommand[]::new));
        setBotCommands();
    }

    private void setBotCommands() {
        SetMyCommands.SetMyCommandsBuilder setMyCommandsBuilder = SetMyCommands.builder();
        for (IBotCommand command : commands) {
            setMyCommandsBuilder.command(new BotCommand(command.getCommandIdentifier(), command.getDescription()));
        }
        try {
            execute(setMyCommandsBuilder.build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void processNonCommandUpdate(Update update) {
        try {
            nonCommandUpdateProcessor.process(update, this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }
}
