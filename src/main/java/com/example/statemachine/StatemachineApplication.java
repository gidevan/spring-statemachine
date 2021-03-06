package com.example.statemachine;

import com.example.statemachine.config.StateMachineConfig;
import com.example.statemachine.event.Events;
import com.example.statemachine.state.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

import java.util.List;

@SpringBootApplication
public class StatemachineApplication implements CommandLineRunner {

	@Autowired
	private StateMachine<States, Events> stateMachine;

	public static void main(String[] args) {
		SpringApplication.run(StatemachineApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		stateMachine.sendEvent(Events.EVENT_INITIAL);
		stateMachine.sendEvent(Events.EVENT1);
		stateMachine.sendEvent(Events.EVENT2);
		stateMachine.sendEvent(Events.EVENT_STATE3_TO_STATE2);
		System.out.println("call EVENT1");
		stateMachine.sendEvent(Events.EVENT1);
		System.out.println("call EVENT2");
		stateMachine.sendEvent(Events.EVENT2);

		System.out.println("----------To Choice----------");
		stateMachine.sendEvent(Events.EVENT_CHOICE);


		List<String> history = (List<String>)stateMachine.getExtendedState().getVariables().get(StateMachineConfig.HISTORY_KEY);
		System.out.println("StateMachine history: ");
		history.forEach(item -> System.out.println("history item: " + item));

		System.out.println("Current state: " + stateMachine.getState().getId());
	}
}
