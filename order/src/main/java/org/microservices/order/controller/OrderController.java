package org.microservices.order.controller;

 import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import org.microservices.order.model.Order;
import org.microservices.order.service.OrderServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = {"Order Controller"})
@RestController
@RequestMapping(value = "/api/orders")
public class OrderController {

    private final OrderServices orderServices;

    public OrderController(OrderServices orderServices) {
        this.orderServices = orderServices;
    }

    @Operation(summary = "Get all orders")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return all existing orders"),
    })
    @GetMapping
    public ResponseEntity<List<Order>> getAll() { //Should return DTO instead of actual entity
        return ResponseEntity.ok().body(orderServices.getAll());
    }

    @Operation(summary = "Get specific order by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return specific order"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) { //Should return DTO instead of actual entity
        return ResponseEntity.ok().body(orderServices.getById(id));
    }

    @Operation(summary = "Save order")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Status of order creation"),
            @ApiResponse(code = 400, message = "Status of order creation")
    })
    @PostMapping
    public ResponseEntity<Order> save(@RequestBody Order order) {
        return ResponseEntity.status(201).body(orderServices.save(order));
    }

    @Operation(summary = "Edit order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return updated order"),
            @ApiResponse(code = 400, message = "Status of order update")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @Valid @RequestBody Order order) {
        return ResponseEntity.ok().body(orderServices.update(id, order)); //Should get and return DTO instead of actual entity
    }

    @Operation(summary = "Deleting order by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Status of order deletion"),
            @ApiResponse(code = 400, message = "Status of order deletion")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderServices.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
