package org.microservices.order.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.microservices.order.controller.UserClient;
import org.microservices.order.exceptions.BadRequestException;
import org.microservices.order.model.Order;
import org.microservices.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderServices{

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final KafkaService kafkaService;

    public OrderServiceImpl(OrderRepository orderRepository, UserClient userClient, KafkaService kafkaService) {
        this.orderRepository = orderRepository;
        this.userClient = userClient;
        this.kafkaService = kafkaService;
    }

    @Override
    public Order save(Order order) {
        if (Objects.isNull(order.getUserId())) {
            throw new BadRequestException("user id is required");
        } else if (!userClient.existsById(order.getUserId())) {
            throw new BadRequestException("user do not exists");
        }
        kafkaService.sendMessage("orderCreated", "created");
        return orderRepository.save(order);
    }

    @Override
    public Order update(Long id, Order order) {
        Order orderToUpdate = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        //Add kafka check that user exists
        orderToUpdate.setProduct(order.getProduct());
        orderToUpdate.setQuantity(order.getQuantity());
        return orderRepository.save(orderToUpdate);
    }

    @Override
    public Order getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}
