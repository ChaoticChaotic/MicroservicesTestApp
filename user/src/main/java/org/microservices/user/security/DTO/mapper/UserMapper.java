package org.microservices.user.security.DTO.mapper;

import org.microservices.user.model.User;
import org.microservices.user.security.DTO.RegistrationRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserMapper {

    public User requestToUser(RegistrationRequest request) {
        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .roles(new ArrayList<>())
                .build();
    }
}
