package org.microservices.order.service;

import org.microservices.order.model.Order;

import java.util.List;

public interface OrderServices {
    Order save(Order order);
    Order update(Long id, Order order);
    Order getById(Long id);
    List<Order> getAll();
    void deleteById(Long id);
}
