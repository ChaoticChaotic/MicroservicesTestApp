package org.microservices.order.controller;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userClient", url = "http://localhost:8000")
public interface UserClient {

    @GetMapping("/api/users/exists/{id}")
    boolean existsById(@PathVariable("id") Long id);
}

