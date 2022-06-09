package com.lexst64.lingvolivetelegrambot;

import com.lexst64.lingvolivetelegrambot.commands.ContextCommand;
import com.lexst64.lingvolivetelegrambot.commands.HelpCommand;
import com.lexst64.lingvolivetelegrambot.commands.LangCommand;
import com.lexst64.lingvolivetelegrambot.commands.StartCommand;
import com.lexst64.lingvolivetelegrambot.processors.CallbackQueryUpdateProcessor;
import com.lexst64.lingvolivetelegrambot.processors.TextMessageUpdateProcessor;
import com.lexst64.lingvolivetelegrambot.processors.UpdateProcessor;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class LingvoLiveTelegramBot extends TelegramLongPollingCommandBot {

    private final static String BOT_USERNAME = "LingvoLiveBot";

    private final String botToken;
    private final UpdateProcessor[] nonCommandUpdateProcessors;
    private final IBotCommand[] commands;

    public LingvoLiveTelegramBot(String botToken) {
        super();
        this.botToken = botToken;
        nonCommandUpdateProcessors = new UpdateProcessor[]{
                new CallbackQueryUpdateProcessor(),
                new TextMessageUpdateProcessor()
        };
        commands = new IBotCommand[]{
                new HelpCommand(),
                new ContextCommand(),
                new StartCommand(),
                new LangCommand()
        };
        registerAll(commands);
        updateBotCommands();
    }

    private void updateBotCommands() {
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
        for (UpdateProcessor updateProcessor : nonCommandUpdateProcessors) {
            boolean isProcessed = updateProcessor.process(update, this);
            if (isProcessed) {
                return;
            }
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
