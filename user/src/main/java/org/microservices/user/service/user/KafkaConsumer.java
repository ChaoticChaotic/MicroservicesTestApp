package org.microservices.user.service.user;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "orderCreated", groupId = "microservices-test")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}
