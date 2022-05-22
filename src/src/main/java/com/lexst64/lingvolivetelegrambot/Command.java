package com.lexst64.lingvolivetelegrambot;

public enum Command {

    START("/start"),
    HELP("/help"),
    MINICARD("/minicard"),
    SUGGESTS("/suggests"),
    WORD_FORMS("/word_forms"),
    WORD_LIST("/word_list"),
    STOP("/stop");

    private final String name;

    Command(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Command getCommandByName(String name) {
        for (Command command : Command.values()) {
            if (command.name.equals(name)) return command;
        }
        throw new IllegalArgumentException("Command enum doesn't have constant with name " + name);
    }
}
