package org.microservices.order.controller;

import org.microservices.order.model.Order;
import org.microservices.order.service.OrderServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/orders")
public class OrderController {

    private final OrderServices orderServices;

    public OrderController(OrderServices orderServices) {
        this.orderServices = orderServices;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAll() { //Should return DTO instead of actual entity
        return ResponseEntity.ok().body(orderServices.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) { //Should return DTO instead of actual entity
        return ResponseEntity.ok().body(orderServices.getById(id));
    }


    @PostMapping
    public ResponseEntity<Order> save(@RequestBody Order order) {
        return ResponseEntity.status(201).body(orderServices.save(order));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @Valid @RequestBody Order order) {
        return ResponseEntity.ok().body(orderServices.update(id, order)); //Should get and return DTO instead of actual entity
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderServices.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
