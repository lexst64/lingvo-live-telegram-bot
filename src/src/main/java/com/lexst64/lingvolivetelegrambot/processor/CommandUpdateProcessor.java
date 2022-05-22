package com.lexst64.lingvolivetelegrambot.processor;

import com.lexst64.lingvolivetelegrambot.Command;
import com.lexst64.lingvolivetelegrambot.CommandHandler;
import com.pengrad.telegrambot.model.Update;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommandUpdateProcessor {

    private final Map<Command, CommandHandler> actions;

    private CommandHandler defaultHandler;

    public CommandUpdateProcessor() {
        actions = new LinkedHashMap<>();
    }

    public void process(Update update, Command command) {
        if (defaultHandler != null) {
            actions.getOrDefault(command, defaultHandler).process(update);
        } else {
            actions.get(command).process(update);
        }
    }

    public void setDefaultHandler(CommandHandler handler) {
        defaultHandler = handler;
    }

    public void processDefaultHandler(Update update) {
        defaultHandler.process(update);
    }

    public void addHandler(Command command, CommandHandler handler) {
        actions.put(command, handler);
    }
}
