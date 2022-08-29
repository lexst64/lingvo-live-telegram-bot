package com.lexst64.lingvolivetelegrambot.processors.callback.handlers;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface CallbackQueryHandler {

    /**
     * Verifies the provided callback query before processing it
     *
     * @return true if callback query is valid or false if it's not
     */
    boolean verifyCallbackQuery(CallbackQuery callbackQuery);

    /**
     * Processes the provided callback query and returns true if
     * it's verified by implemented verifyCallbackQuery() method
     *
     * @return true if callback query is valid and has been processed,
     * otherwise false if callback query is considered invalid and
     * has not been processed.
     */
    boolean process(CallbackQuery callbackQuery, AbsSender absSender);

    /**
     * @return regex string that is used for verifying callback query data
     * in implemented verifyCallbackQuery() method
     */
    String getRegex();
}
