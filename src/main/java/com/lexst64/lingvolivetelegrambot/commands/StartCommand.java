package com.lexst64.lingvolivetelegrambot.commands;

import com.lexst64.lingvoliveapi.lang.LangPair;
import com.lexst64.lingvolivetelegrambot.providers.HelpMessageProvider;
import com.lexst64.lingvolivetelegrambot.database.DBManager;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartCommand extends BotCommand {

    private final static LangPair DEFAULT_LANG_PAIR = LangPair.EN_RU;
    private final DBManager dbManager;

    public StartCommand() {
        super("start", "use it to start the bot");
        dbManager = DBManager.getInstance();
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        try {
            boolean isDone = dbManager.createNewUser(user.getId(), DEFAULT_LANG_PAIR);
            if (isDone) {
                absSender.execute(new HelpMessageProvider().provide(chat.getId()));
            } else {
                absSender.execute(new SendMessage(chat.getId().toString(), "smt went wrong"));
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
