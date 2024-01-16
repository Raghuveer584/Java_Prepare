package com.etcc.mendix.camunda;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;

import com.etcc.kafka.enums.SchemaTypeEnum;
import com.etcc.kafka.exception.MissingAvroSchemaFileException;
import com.etcc.kafka.producer.GenericKafkaProducer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
public class KafkaProducer extends GenericKafkaProducer implements IKafkaProducer{

	private static String avroSchema = "{\r\n"
			+ "	\"namespace\": \"com.etcc.kafka.avro.generator.model\",\r\n"
			+ "	\"type\": \"record\",\r\n"
			+ "	\"name\": \"CamundaAvroEntity\",\r\n"
			+ "	\"doc\": \"Represents an object is sent from Camunda\",\r\n"
			+ "	\"fields\": [\r\n"
			+ "		{\r\n"
			+ "			\"name\": \"message\",\r\n"
			+ "			\"type\": \"string\",\r\n"
			+ "			\"doc\": \"generic message\"\r\n"
			+ "		}\r\n"
			+ "	]\r\n"
			+ "}";
	
	private String message;

	public KafkaProducer(String message,
			String topic, String clientId,  String transactionId,
			boolean idempotence, String bootstrapServers, String schemaRegistryURL) throws MissingAvroSchemaFileException {
		super(bootstrapServers, topic, clientId, schemaRegistryURL, null, idempotence, avroSchema, SchemaTypeEnum.STRING,
				StringSerializer.class, KafkaAvroSerializer.class);
		this.message = message;
	}

	@Override
	protected GenericRecord buildGenericRecord() {
		GenericRecord result = null;
		GenericRecordBuilder builder = new GenericRecordBuilder(this.schema);
		builder.set("message", this.message);
		result = builder.build();
		return result;
	}
	
	public void sendRecord() throws Exception {
		send();
	}
}
