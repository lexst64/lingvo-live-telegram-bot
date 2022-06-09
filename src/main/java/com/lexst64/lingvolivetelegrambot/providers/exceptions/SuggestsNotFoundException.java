package com.lexst64.lingvolivetelegrambot.providers.exceptions;

public class SuggestsNotFoundException extends RuntimeException {
    public SuggestsNotFoundException(String text) {
        super("suggests not found for '" + text + "'");
    }
}
