package io.flickd.twitter;

import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.schema.GenericRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;

import io.flickd.twitter.service.MyMessageListener;

@Configuration
@Profile("astra")
public class AstraStreamingConfiguration {

	private PulsarClient client;

	@Value("${astra-streaming.service-url}")
	String serviceUrl;
	
	@Value("${astra-streaming.token}")
	String token;
	
	@Value("${astra-streaming.topic.from-cdc-data}")
	String cdcData;
	
	@Bean
	public MessageListener<GenericRecord> myMessageListener() {
		return new MyMessageListener<GenericRecord>();
		}
	
	@Bean
	public Consumer<GenericRecord> consumer() throws PulsarClientException {
		client = PulsarClient.builder().serviceUrl(serviceUrl)
				.authentication(AuthenticationFactory.token(token)).build();
		return client.newConsumer(Schema.AUTO_CONSUME()).topic(cdcData)
				.subscriptionName("cdc-tweet-subscription")
				.messageListener(myMessageListener()).subscribe();
	}

	@Bean
	public Jackson2JsonObjectMapper jackson2JsonObjectMapper() {
		return new Jackson2JsonObjectMapper();
	}
}
