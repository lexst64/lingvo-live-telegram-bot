package com.lexst64.lingvolivetelegrambot.api;

public class HelpDataProvider {
    public String provide() {
        return """
                Hi! I'm a translator bot that can translate words and phrases.
                You can change your language pair using /lang command.
                
                /help - help info
                /context - context for passed word/phrase
                /lang - change the language pair
                """;
    }
}
