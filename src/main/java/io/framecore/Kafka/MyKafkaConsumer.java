package io.framecore.Kafka;

import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import io.framecore.Frame.Environment;
import io.framecore.Frame.Log;
import io.framecore.Tool.PropertiesHelp;

public class MyKafkaConsumer {
	
	
 
	public static KafkaConsumer<String, String> getConsumerAuth(String module,String groupId)   {
		
		Properties props = new Properties(); 
		
		try {
			String jaasFile = PropertiesHelp.getFilePath(PropertiesHelp.getApplicationConf("kafka."+module+".kafka_client_jaas"));
			
			System.setProperty("java.security.auth.login.config", jaasFile); 
			
			props.setProperty ("security.protocol", "SASL_PLAINTEXT"); 

			props.setProperty ("sasl.mechanism", "PLAIN");  
						
			// 服务器ip:端口号，集群用逗号分隔
					
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, PropertiesHelp.getApplicationConf("kafka."+module+".bootstrap_servers_config"));
			
			//这个就是kafka消费组即CG，相同的group id 只会消费一次，即所谓的queue，不同的group id就是所谓的发布/订阅   props.put("bootstrap.servers", "localhost:9092");
			// 消费者指定组，名称可以随意，注意相同消费组中的消费者只能对同一个分区消费一次

			props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId); 
			
			props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); // 是否启用自动提交，默认true 
			props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000); // 自动提交间隔时间1s
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // key反序列化指定类
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());  // value反序列化指定类，注意生产者与消费者要保持一致，否则解析出问题
			
			props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5000); 
			
			
			
			KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(props);
			return kafkaConsumer;
			
		} catch (Exception e) {
			 Log.logError(e);
		}
		return null;
	
	}
	
	
	public static KafkaConsumer<String, String> getConsumerAuthByType(String module,String businessType)   {
		 
		return  getConsumerAuth(module,businessType+Environment.getCurrentGroupId());
	}
	
	
	
	
	public static KafkaConsumer<String, String> getConsumer(String groupId)   {
		
		Properties props = new Properties(); 
		
		try {
						
			// 服务器ip:端口号，集群用逗号分隔
					
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, PropertiesHelp.getAppConf("kafka.bootstrap_servers_config"));
			
			//这个就是kafka消费组即CG，相同的group id 只会消费一次，即所谓的queue，不同的group id就是所谓的发布/订阅   props.put("bootstrap.servers", "localhost:9092");
			// 消费者指定组，名称可以随意，注意相同消费组中的消费者只能对同一个分区消费一次

			props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId); 
			
			props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); // 是否启用自动提交，默认true 
			props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000); // 自动提交间隔时间1s
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // key反序列化指定类
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());  // value反序列化指定类，注意生产者与消费者要保持一致，否则解析出问题
			
			props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5000); 
			
			
			
			KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(props);
			return kafkaConsumer;
			
		} catch (Exception e) {
			 Log.logError(e);
		}
		return null;
	
	}
	
	public static KafkaConsumer<String, String> getConsumer()
	{
		return  getConsumer(Environment.getCurrentGroupId());
	}
	
	public static KafkaConsumer<String, String> getConsumerByType(String businessType)
	{
		return  getConsumer(businessType+Environment.getCurrentGroupId());
	}

}
