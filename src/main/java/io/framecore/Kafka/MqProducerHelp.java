package io.framecore.Kafka;


import java.io.IOException;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import io.framecore.Tool.PropertiesHelp;

public class MqProducerHelp {
	
	
	static KafkaProducer<String, String> producer = getProducer();// getProducer();
	
	public static void send(String topic,String message)
	{
		producer.send(new ProducerRecord<String, String>(topic, message));
	}

 
	public static KafkaProducer<String, String> getProducerAuth(String module)
	{

		try {
			String jaasFile = PropertiesHelp.getFilePath(PropertiesHelp.getApplicationConf("kafka."+module+".kafka_client_jaas"));
		
			System.setProperty("java.security.auth.login.config", jaasFile); 
		
			Properties props = new Properties(); // 服务器ip:端口号，集群用逗号分隔
					
			props.setProperty ("security.protocol", "SASL_PLAINTEXT");

			props.setProperty ("sasl.mechanism", "PLAIN");
			
			props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,PropertiesHelp.getApplicationConf("kafka."+module+".bootstrap_servers_config"));

			// key序列化指定类
			props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); // 是否启用自动提交，默认true
			props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000); // 自动提交间隔时间1s
			props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // key反序列化指定类
			props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // value序列化指定类
			
 

			KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props); // 向test_topic发送hello,
																								// kafka
			return producer;

		} catch (IOException e) {
			 
			e.printStackTrace();
		}
		return null;
		
		
		 
	}

	private static KafkaProducer<String, String> getProducer() {
		
		Properties props = new Properties(); // 服务器ip:端口号，集群用逗号分隔
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,PropertiesHelp.getAppConf("kafka.bootstrap_servers_config"));

		// key序列化指定类
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); // 是否启用自动提交，默认true
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000); // 自动提交间隔时间1s
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // key反序列化指定类
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // value序列化指定类
		
 

		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props); // 向test_topic发送hello,
																							// kafka
		return producer;
 
	}

}

