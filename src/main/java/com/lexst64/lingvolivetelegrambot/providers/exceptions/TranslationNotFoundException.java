package com.lexst64.lingvolivetelegrambot.providers.exceptions;

public class TranslationNotFoundException extends RuntimeException {
    public TranslationNotFoundException(String text) {
        super("translation not found for '" + text + "'");
    }
}
