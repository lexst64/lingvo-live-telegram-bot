package com.lexst64.lingvolivetelegrambot;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;

public class StateMachineTest {

    private enum State {
        DEFAULT, MINICARD, TRANSLATE
    }

    private enum Trigger {
        START_MINICARD, STOP_MINICARD, MESSAGE, TRANSLATE_RETURN
    }

    public static void main(String[] args) {
        var config = new StateMachineConfig<State, Trigger>();

        config.configure(State.DEFAULT)
                .onEntry(StateMachineTest::setDefaults)
                .onExit(StateMachineTest::removeDefaults)
                .permit(Trigger.START_MINICARD, State.MINICARD);

        config.configure(State.MINICARD)
                .onEntry(StateMachineTest::setMinicards)
                .onExit(StateMachineTest::removeMinicards)
                .permit(Trigger.MESSAGE, State.TRANSLATE)
                .permit(Trigger.STOP_MINICARD, State.DEFAULT);

        config.configure(State.TRANSLATE)
                .onEntry(StateMachineTest::translate)
                .permit(Trigger.TRANSLATE_RETURN, State.MINICARD);

        var fsm = new StateMachine<>(State.DEFAULT, config);
        fsm.fireInitialTransition();
        fsm.fire(Trigger.START_MINICARD);
        fsm.fire(Trigger.MESSAGE);
        fsm.fire(Trigger.TRANSLATE_RETURN);
        fsm.fire(Trigger.MESSAGE);
        fsm.fire(Trigger.TRANSLATE_RETURN);
        fsm.fire(Trigger.STOP_MINICARD);
    }

    private static void setDefaults() {
        System.out.println("setDefaults");
    }

    private static void removeDefaults() {
        System.out.println("removeDefaults");
    }

    private static void setMinicards() {
        System.out.println("setMinicards");
    }

    private static void removeMinicards() {
        System.out.println("removeMinicards");

    }

    private static void translate() {
        System.out.println("translate");
    }

}
