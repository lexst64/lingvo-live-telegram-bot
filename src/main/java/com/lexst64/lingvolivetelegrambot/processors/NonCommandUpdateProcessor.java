package com.lexst64.lingvolivetelegrambot.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface NonCommandUpdateProcessor {

    /**
     * Verifies the provided update before processing it
     *
     * @return true if update is valid or false if it's not
     */
    boolean verifyUpdate(Update update);

    /**
     * Processes the provided update and returns true if
     * it's verified by implemented verifyUpdate() method
     *
     * @return true if update is valid and has been processed,
     * otherwise false if update is considered invalid and
     * has not been processed.
     */
    boolean process(Update update, AbsSender absSender);
}
