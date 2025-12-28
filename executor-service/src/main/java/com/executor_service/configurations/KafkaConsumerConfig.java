package com.executor_service.configurations;

import com.executor_service.models.JobEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final ServiceConfiguration serviceConfiguration;
    private final ObjectMapper objectMapper;

    @Bean
    public ConsumerFactory<String, JobEvent> consumerFactory() {

        Map<String, Object> props = new HashMap<>();
        ServiceConfiguration.Kafka kafka = serviceConfiguration.getKafka();
        ServiceConfiguration.Kafka.Consumer consumer = kafka.getConsumer();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG,consumer.getGroupId());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,consumer.isEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,consumer.getAutoOffsetReset());
        JsonDeserializer<JobEvent> valueDeserializer = new JsonDeserializer<>(JobEvent.class, objectMapper);
        valueDeserializer.ignoreTypeHeaders();
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, JobEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, JobEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, JobEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3);
        factory.getContainerProperties()
                .setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}


