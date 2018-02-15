package com.example.statemachine;

import com.example.statemachine.event.Events;
import com.example.statemachine.state.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

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
	}
}
