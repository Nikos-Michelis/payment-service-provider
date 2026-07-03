package com.stripe.payment_service_provider.payment.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class KafkaMessageProducerImpl implements KafkaMessageProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaMessageProducerImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Override
    public void send(String topic, Object payload) throws ExecutionException, InterruptedException, TimeoutException {
        kafkaTemplate.send(topic, payload);
    }
    @Override
    public void send(String topic, String key, Object payload) throws ExecutionException, InterruptedException, TimeoutException {
        kafkaTemplate.send(topic, key, payload);
    }
    @Override
    public SendResult<String, Object> sendSync(String topic, String key, Object payload, long timeoutSeconds) throws ExecutionException, InterruptedException, TimeoutException {
        return kafkaTemplate.send(topic, key, payload).get(timeoutSeconds, TimeUnit.SECONDS);
    }
}
