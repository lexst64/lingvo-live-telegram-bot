package com.lexst64.lingvolivetelegrambot.providers.exceptions;

public class ContextsNotFoundException extends RuntimeException {
    public ContextsNotFoundException(String text) {
        super("contexts not found for '" + text + "'");
    }
}
