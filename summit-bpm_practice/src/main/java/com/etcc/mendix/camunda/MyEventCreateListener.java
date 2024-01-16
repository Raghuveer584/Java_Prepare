package com.etcc.mendix.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.extension.reactor.bus.CamundaSelector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@CamundaSelector(event = ExecutionListener.EVENTNAME_START)
@DependsOn("reactorPlugin")
public class MyEventCreateListener implements ExecutionListener {

	@Value("${mendix.url}")
	private String mendixServerUrl;
	
	@Value("${adapter.url}")
	private String adapterServerUrl;
	
	@Value("${fulfillment.url}")
	private String fulfillmentUrl;
	
	@Value("${riteNotification.pendingFulfillment.timer}")
	private String pendingFulfillmentTimer;
	
	@Value("${riteNotification.pendingFulfillment.receipient}")
	private String pendingFulfillmentReceipient;

	

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		execution.setVariable("mendixUrl", mendixServerUrl);
		execution.setVariable("adapterUrl", adapterServerUrl);
		execution.setVariable("fulfillmentUrl", fulfillmentUrl);
		execution.setVariable("pendingFulfillmentTimer", pendingFulfillmentTimer);
		execution.setVariable("pendingFulfillmentReceipient", pendingFulfillmentReceipient);
	}

}