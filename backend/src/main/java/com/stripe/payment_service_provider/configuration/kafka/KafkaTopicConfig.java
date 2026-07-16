package com.stripe.payment_service_provider.configuration.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.topics.products}")
    private String productsTopic;
    @Value("${spring.kafka.topics.customers}")
    private String customersTopic;
    @Value("${spring.kafka.topics.payments}")
    private String paymentsTopic;

    @Bean
    public NewTopic productsTopic() {
        return TopicBuilder.name(productsTopic)
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic customersTopic() {
        return TopicBuilder.name(customersTopic)
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic paymentsTopic() {
        return TopicBuilder.name(paymentsTopic)
                .partitions(3)
                .build();
    }
}
