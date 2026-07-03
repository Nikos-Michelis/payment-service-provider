package com.stripe.payment_service_provider.payment.producer;

import org.springframework.kafka.support.SendResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface KafkaMessageProducer {
    void send(String topic, Object payload) throws ExecutionException, InterruptedException, TimeoutException;
    void send(String topic, String key, Object payload) throws ExecutionException, InterruptedException, TimeoutException;
    SendResult<String, Object> sendSync(String topic, String key, Object payload, long timeoutSeconds) throws ExecutionException, InterruptedException, TimeoutException;
}
