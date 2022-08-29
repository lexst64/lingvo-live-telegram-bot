package com.lexst64.lingvolivetelegrambot.api.exceptions;

public class SuggestsNotFoundException extends RuntimeException {
    public SuggestsNotFoundException(String text) {
        super("suggests not found for '" + text + "'");
    }
}
