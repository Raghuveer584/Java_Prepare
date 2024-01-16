package com.etcc.mendix.camunda;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.etcc.notification.SendNotificationEventToKafka;

@Service("kafkaExecutor")
public class KafkaExecutorDelegate implements JavaDelegate {

	@Value(value = "${kafka.bootstrapServers}")
	private String bootstrapServer;

	@Value(value = "${kafka.schemaRegistryURL}")
	private String schemaRegistryURL;
	
	@Value(value = "${riteNotification.topic.name}")
	private String riteNotificationTopic;
	
	@Value(value = "${riteNotification.client.id}")
	private String riteNotificationClientID;
	
	@Value(value = "${riteNotification.transaction.id}")
	private String riteNotificationTransactionID;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.out.println("bootstrapServer " + bootstrapServer);
		System.out.println("schemaRegistryURL " + schemaRegistryURL);
	}

	public void send(String message, String bootstrapServers, String schemaRegistryURL, String topic, String clientId, String transactionId,
			String idempotenceStr) throws Exception {
	
		boolean idempotence = "Y".equalsIgnoreCase(idempotenceStr) ? true : false;
		if (StringUtils.isEmpty(bootstrapServers)) {
			bootstrapServers = this.bootstrapServer;
		}
		
		if (StringUtils.isEmpty(schemaRegistryURL)) {
			schemaRegistryURL = this.schemaRegistryURL;
		}
		
		System.out.println("message " + message);
		System.out.println("topic " + topic);
		System.out.println("clientId " + clientId);
		System.out.println("transactionId " + transactionId);
		System.out.println("idempotence " + idempotence);
		System.out.println("bootstrapServers " + bootstrapServers);
		System.out.println("schemaRegistryURL " + schemaRegistryURL);
		
		KafkaProducer producer = new KafkaProducer(message, topic, clientId, transactionId, idempotence,
				bootstrapServers, schemaRegistryURL);
		producer.sendRecord();
	}

	public void sendToCRMForNotification(String bootstrapServers, String schemaRegistryURL, String topic, String clientId, String transactionId,
			String idempotenceStr, String eventName, String notificationMedia, String notificationPriority, String jsonData, String caseNumber) throws Exception {
	
		boolean idempotence = "Y".equalsIgnoreCase(idempotenceStr) ? true : false;
		if (StringUtils.isEmpty(bootstrapServers)) {
			bootstrapServers = this.bootstrapServer;
		}
		
		if (StringUtils.isEmpty(schemaRegistryURL)) {
			schemaRegistryURL = this.schemaRegistryURL;
		}
		
		System.out.println("topic " + topic);
		System.out.println("clientId " + clientId);
		System.out.println("transactionId " + transactionId);
		System.out.println("idempotence " + idempotence);
		System.out.println("bootstrapServers " + bootstrapServers);
		System.out.println("schemaRegistryURL " + schemaRegistryURL);
		System.out.println("eventName " + eventName);
		System.out.println("caseNumber " + caseNumber);
		System.out.println("notificationMedia " + notificationMedia);
		System.out.println("notificationPriority " + notificationPriority);
		System.out.println("jsonData " + jsonData);
		
		HashMap<String, String> map = new HashMap<>();
		map.put("eventName", eventName);
		map.put("notificationMedia", notificationMedia);
		map.put("notificationPriority", notificationPriority);
		map.put("caseNumber", caseNumber);
		map.put("jsonData", jsonData);
		
		ObjectMapper mapperObj = new ObjectMapper();
		String message = mapperObj.writeValueAsString(map);
		KafkaProducer producer = new KafkaProducer(message, topic, clientId, transactionId, idempotence,
				bootstrapServers, schemaRegistryURL);
		producer.sendRecord();
	}
	
	public void sendNotification(String eventName, String notificationMedia, String notificationPriority, String jsonData) throws Exception {
	
		//{"AccountNumber":"0000000000000353","AddressLine1":"11157 Medallion Lane","City":"Frisco"," DateOfIssueDay":"12/12/2003","FirstName":"NEERUDU","LastName":"VEERAIAH","PostalCode":"75035","State":"TEXAS","ReceipentEmailAddress":"sfdsdfs@etcc.com"}
		System.out.println("eventName " + eventName);
		System.out.println("topic " + this.riteNotificationTopic);
		System.out.println("clientId " + this.riteNotificationClientID);
		System.out.println("transactionId " + this.riteNotificationTransactionID);
		System.out.println("bootstrapServers " + this.bootstrapServer);
		System.out.println("schemaRegistryURL " + this.schemaRegistryURL);
		
		System.out.println("eventName " + eventName);
		System.out.println("notificationMedia " + notificationMedia);
		System.out.println("notificationPriority " + notificationPriority);
		System.out.println("jsonData " + jsonData);
		
		
		SendNotificationEventToKafka notifier = new SendNotificationEventToKafka(this.bootstrapServer, this.riteNotificationTopic, this.riteNotificationTopic, this.schemaRegistryURL, this.riteNotificationTransactionID);
		notifier.sendNotification(eventName, notificationMedia, notificationPriority, jsonData);
		
	}
	
}
