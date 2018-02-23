package com.example.statemachine.config;

import com.example.statemachine.event.Events;
import com.example.statemachine.state.States;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Configuration
@EnableStateMachine
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

    private static final int CHOICE_VALUE = 81;
    public static final String HISTORY_KEY = "history";

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config)
            throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states)
            throws Exception {
        states
                .withStates()
                .initial(States.STATE_INITIAL)
                .choice(States.STATE_CHOICE)
                .states(EnumSet.allOf(States.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        transitions
                .withExternal()
                        .source(States.STATE1).target(States.STATE2).event(Events.EVENT1)
                        .action(stateContext -> {
                            addStateMachineHistory(stateContext);
                            System.out.println("Action: STATE1 -> STATE2");
                        })
                .and().withExternal()
                            .source(States.STATE2).target(States.STATE3).event(Events.EVENT2)
                            .action(stateContext -> {
                                addStateMachineHistory(stateContext);
                                System.out.println("Action: STATE2 -> STATE3");
                            })
                .and().withExternal()
                            .source(States.STATE_INITIAL).target(States.STATE1).event(Events.EVENT_INITIAL)
                            .action(stateContext -> addStateMachineHistory(stateContext))
                .and().withExternal()
                            .source(States.STATE3).target(States.STATE2).event(Events.EVENT_STATE3_TO_STATE2)
                            .action(stateContext -> addStateMachineHistory(stateContext))
                .and().withExternal()
                            .source(States.STATE3).target(States.STATE_CHOICE).event(Events.EVENT_CHOICE)
                .and().withChoice()
                        .source(States.STATE_CHOICE)
                        .first(States.STATE_CHOICE1, choice1Guard())
                        .then(States.STATE_CHOICE2, context -> CHOICE_VALUE < 7)
                        .then(States.STATE_CHOICE3, context -> CHOICE_VALUE < 9)
                        .last(States.STATE_CHOICE_DEFAULT);
    }

    public Guard<States, Events> choice1Guard() {
        return context -> {
            System.out.println("Choice: 1");
            addStateMachineHistory(context);
            return CHOICE_VALUE < 5;
        };
    }

    @Bean
    public StateMachineListener<States, Events> listener() {
        return new StateMachineListenerAdapter<States, Events>() {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to) {
                System.out.println("State change to " + to.getId());
            }
        };
    }

    private static void addStateMachineHistory(StateContext<States, Events> stateContext) {

        StateMachine<States, Events> stateMachine = stateContext.getStateMachine();
        List<String> history = (List<String>)stateMachine.getExtendedState().getVariables().get(HISTORY_KEY);
        if(history == null) {
            history = new ArrayList<>();
            stateMachine.getExtendedState().getVariables().put(HISTORY_KEY, history);
        }
        System.out.println("add state machine history: " + stateMachine.getState());
        history.add(stateMachine.getState().getId().toString());
    }
}
