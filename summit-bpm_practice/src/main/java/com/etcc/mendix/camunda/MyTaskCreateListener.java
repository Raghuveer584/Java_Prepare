package com.etcc.mendix.camunda;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.extension.reactor.bus.CamundaSelector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@CamundaSelector(event = TaskListener.EVENTNAME_CREATE)
@DependsOn("reactorPlugin")
public class MyTaskCreateListener implements TaskListener {

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
	public void notify(DelegateTask delegateTask) {
		delegateTask.setVariable("mendixUrl", mendixServerUrl);
		delegateTask.setVariable("adapterUrl", adapterServerUrl);
		delegateTask.setVariable("fulfillmentUrl", fulfillmentUrl);
		delegateTask.setVariable("pendingFulfillmentTimer", pendingFulfillmentTimer);
		delegateTask.setVariable("pendingFulfillmentReceipient", pendingFulfillmentReceipient);
	}

}