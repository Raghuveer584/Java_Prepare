package com.etcc.mendix.camunda;

import org.camunda.bpm.extension.reactor.CamundaReactor;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableProcessApplication
@PropertySource(value="file:config/app.properties",ignoreResourceNotFound=true)
public class CamundaApplication {

	@Autowired
	private MyTaskCreateListener taskListener;
	
	@Autowired
	private MyEventCreateListener eventListener;
	
	@Autowired
	Environment env;


	public static void main(String... args) {
		SpringApplication.run(CamundaApplication.class, args);
	}

	@EventListener
	private void processPostDeploy(PostDeployEvent event) {		
		CamundaReactor.eventBus().register(taskListener);
		CamundaReactor.eventBus().register(eventListener);
		
	}
}
