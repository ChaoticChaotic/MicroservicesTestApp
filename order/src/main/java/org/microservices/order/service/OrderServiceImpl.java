package org.microservices.order.service;

import org.microservices.order.model.Order;
import org.microservices.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderServices{

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order save(Order order) {
        //Add kafka check that user exists
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
