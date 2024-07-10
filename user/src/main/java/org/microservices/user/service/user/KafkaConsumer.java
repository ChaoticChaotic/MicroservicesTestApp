package org.microservices.user.service.user;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class KafkaConsumer {

    private final UserService userService;

    private static final String USER_KEY = "user";
    private static final String ORDER_KEY = "order";

    public KafkaConsumer(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(topics = "orderCreated", groupId = "microservices-test")
    public void listen(String message) {
        Long userId = null;
        String orderId = null;
        for (String part : message.split(",")) {
            if (part.startsWith(USER_KEY)) {
                userId = Long.parseLong(part.substring(part.indexOf("=") + 1));
            } else if (part.startsWith(ORDER_KEY)) {
                orderId = part.substring(part.indexOf("=") + 1);
            }
        }
        //add additional check that long converted as expected
        if (Objects.nonNull(userId) && Objects.nonNull(orderId)) {}
        userService.addOrderId(userId, orderId);
    }
}
