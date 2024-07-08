package org.microservices.user.controller;

import org.microservices.user.model.User;
import org.microservices.user.security.DTO.ChangePassRequest;
import org.microservices.user.security.DTO.RegistrationRequest;
import org.microservices.user.security.DTO.TokensDTO;
import org.microservices.user.security.DTO.mapper.UserMapper;
import org.microservices.user.security.util.TokenUtil;
import org.microservices.user.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping(value = "/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> getAll() { //Should return DTO instead of actual entity
        return ResponseEntity.ok().body(userService.getAll());
    }
}
