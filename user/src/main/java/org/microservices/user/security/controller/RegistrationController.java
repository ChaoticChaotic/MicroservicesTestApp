package org.microservices.user.security.controller;

import org.microservices.user.security.DTO.RegistrationRequest;
import org.microservices.user.service.registration.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping(value = "/auth/registration")
    public ResponseEntity<Void> register(@RequestBody @Valid RegistrationRequest request) {
        registrationService.register(request);
        return ResponseEntity.status(201).build();
    }
}
