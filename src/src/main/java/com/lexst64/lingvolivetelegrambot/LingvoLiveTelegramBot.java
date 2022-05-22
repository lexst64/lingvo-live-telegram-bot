package com.lexst64.lingvolivetelegrambot;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.delegates.Action2;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters2;
import com.lexst64.lingvoliveapi.LingvoLive;
import com.lexst64.lingvoliveapi.request.GetMinicard;
import com.lexst64.lingvoliveapi.request.GetSuggests;
import com.lexst64.lingvoliveapi.response.GetMinicardResponse;
import com.lexst64.lingvoliveapi.response.GetSuggestsResponse;
import com.lexst64.lingvoliveapi.type.Lang;
import com.lexst64.lingvolivetelegrambot.processor.CommandUpdateProcessor;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;

public class LingvoLiveTelegramBot {

    private final TelegramBot telegramBot;
    private final LingvoLive lingvoLive;
    private final CommandUpdateProcessor commandProcessor;

    private final StateMachineConfig<State, Trigger> config;
    private final StateMachine<State, Trigger> fsm;

    public enum State {
        DEFAULT, MINICARD, MINICARD_TRANSLATING
    }

    public enum Trigger {
        START_MINICARD, STOP, TRANSLATE, FINISH_TRANSLATE
    }

    public LingvoLiveTelegramBot() {
        telegramBot = new TelegramBot(System.getenv("BOT_TOKEN"));
        lingvoLive = new LingvoLive(System.getenv("TEST_API_KEY"));
        telegramBot.setUpdatesListener(getDefaultUpdatesListener());

        config = new StateMachineConfig<>();
        configureStateMachine();
        fsm = new StateMachine<>(State.DEFAULT, config);
        fsm.fireInitialTransition();
        commandProcessor = new CommandUpdateProcessor();
        setupCommandProcessor();
    }

    private void configureStateMachine() {
        config.configure(State.DEFAULT)
                .onEntry(() -> telegramBot.execute(getDefaultCommands()))
                .onEntryFrom(new TriggerWithParameters1<>(Trigger.STOP, Long.class), new Action1<Long>() {
                    @Override
                    public void doIt(Long chatId) {
                        telegramBot.execute(getDefaultCommands());
                        onStop(chatId);
                    }
                })
                .permit(Trigger.START_MINICARD, State.MINICARD);

        config.configure(State.MINICARD)
                .onEntryFrom(new TriggerWithParameters1<>(Trigger.START_MINICARD, Long.class), new Action1<Long>() {
                    @Override
                    public void doIt(Long chatId) {
                        onMinicard(chatId);
                    }
                })
                .permit(Trigger.STOP, State.DEFAULT)
                .permit(Trigger.TRANSLATE, State.MINICARD_TRANSLATING);

        config.configure(State.MINICARD_TRANSLATING)
                .onEntryFrom(new TriggerWithParameters2<>(Trigger.TRANSLATE, Long.class, String.class), new Action2<Long, String>() {
                    @Override
                    public void doIt(Long chatId, String text) {
                        minicardTranslation(chatId, text);
                        fsm.fire(Trigger.FINISH_TRANSLATE);
                    }
                })
                .permit(Trigger.FINISH_TRANSLATE, State.MINICARD);
    }

    private void setupCommandProcessor() {
        commandProcessor.setDefaultHandler(update -> {
            telegramBot.execute(new SendMessage(getChatId(update), "I don't know this command"));
        });

        commandProcessor.addHandler(Command.START, update -> {
            if (fsm.getState() != State.DEFAULT) return;
            onHelp(getChatId(update));
        });

        commandProcessor.addHandler(Command.MINICARD, update -> {
            if (fsm.getState() != State.DEFAULT) return;
            fsm.fire(new TriggerWithParameters1<>(Trigger.START_MINICARD, Long.class), getChatId(update));
        });

        commandProcessor.addHandler(Command.SUGGESTS, update -> {
            if (fsm.getState() != State.DEFAULT) return;
            telegramBot.execute(new SendMessage(getChatId(update), "suggests"));
        });

        commandProcessor.addHandler(Command.STOP, update -> {
            if (fsm.getState() != State.MINICARD) return;
            fsm.fire(new TriggerWithParameters1<>(Trigger.STOP, Long.class), getChatId(update));
        });
    }

    private long getChatId(Update update) {
        return update.message().chat().id();
    }

    private boolean isCommandUpdate(Update update) {
        return update.message() != null
                && update.message().entities() != null
                && update.message().entities().length == 1
                && update.message().entities()[0].type() == MessageEntity.Type.bot_command;
    }

    private void processNonCommandUpdate(Update update) {
        State state = fsm.getState();
        long chatId = getChatId(update);

        if (state == State.DEFAULT) {
            if (isPlainTextUpdate(update) && !isBotUpdate(update)) {
                String text = update.message().text();
                telegramBot.execute(new SendMessage(chatId, text));
            }
        } else if (state == State.MINICARD) {
            if (isPlainTextUpdate(update) && !isBotUpdate(update)) {
                String text = update.message().text();
                fsm.fire(new TriggerWithParameters2<>(Trigger.TRANSLATE, Long.class, String.class), chatId, text);
            }
        }
    }

    private void minicardTranslation(long chatId, String text) {
        GetMinicard minicardRequest = new GetMinicard().text(text).srcLang(Lang.ENGLISH).dstLang(Lang.RUSSIAN);
        GetMinicardResponse minicardResponse = lingvoLive.execute(minicardRequest);
        if (minicardResponse.isOk()) {
            telegramBot.execute(new SendMessage(chatId, minicardResponse.translation().translation()));
            return;
        }

        GetSuggests suggestsRequest = new GetSuggests().text(text).srcLang(Lang.ENGLISH).dstLang(Lang.RUSSIAN);
        GetSuggestsResponse suggestsResponse = lingvoLive.execute(suggestsRequest);

        if (suggestsResponse.isOk()) {
            StringBuilder suggests = new StringBuilder("Maybe you meant:\n");
            for (String suggest : suggestsResponse.suggests()) {
                suggests.append(suggest).append("\n");
            }
            telegramBot.execute(new SendMessage(chatId, suggests.toString()));
        } else {
            telegramBot.execute(new SendMessage(chatId, "word not recognized"));
        }
    }

    private void onHelp(long chatId) {
        telegramBot.execute(new SendMessage(chatId, """
                Hi! I'm a translator bot
                
                Here's my commands:
                
                /minicard
                /suggests
                /word_forms
                /word_list
                /help
                """));
    }

    private void onStop(long chatId) {
        telegramBot.execute(new SendMessage(chatId, "stop minicard"));
    }

    private void onMinicard(long chatId) {
        telegramBot.execute(getMinicardCommands());
        KeyboardButton[][] keyboardButtons = {
                {new KeyboardButton("eng"), new KeyboardButton("="), new KeyboardButton("ru")}
        };
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(keyboardButtons).resizeKeyboard(true);

        telegramBot.execute(new SendMessage(chatId, "Type:").replyMarkup(keyboard));
    }

    private UpdatesListener getDefaultUpdatesListener() {
        return updates -> {
            for (Update update : updates) {
                System.out.println(update);
                if (update.message() == null) {
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }
                if (isCommandUpdate(update)) {
                    String commandName = update.message().text();
                    try {
                        commandProcessor.process(update, Command.getCommandByName(commandName));
                    } catch (IllegalArgumentException e) {
                        commandProcessor.processDefaultHandler(update);
                    }
                } else {
                    processNonCommandUpdate(update);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        };
    }

    private boolean isBotUpdate(Update update) {
        // use asserts?
        return update.message() != null && update.message().from().isBot();
    }

    private boolean isPlainTextUpdate(Update update) {
        return update.message() != null && update.message().entities() == null && update.message().text() != null;
    }

    private SetMyCommands getDefaultCommands() {
        return new SetMyCommands(
                new BotCommand(Command.MINICARD.getName(), "get simplified translation"),
                new BotCommand(Command.SUGGESTS.getName(), "get suggests for word"),
                new BotCommand(Command.WORD_FORMS.getName(), "get froms for word"),
                new BotCommand(Command.WORD_LIST.getName(), "s")
        );
    }

    private SetMyCommands getMinicardCommands() {
        return new SetMyCommands(
                new BotCommand(Command.STOP.getName(), "stop minicard mode")
        );
    }

    public static void main(String[] args) {
        new LingvoLiveTelegramBot();
    }
}
