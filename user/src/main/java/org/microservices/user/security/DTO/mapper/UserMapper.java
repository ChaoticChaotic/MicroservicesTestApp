package org.microservices.user.security.DTO.mapper;

import org.microservices.user.model.User;
import org.microservices.user.security.DTO.AuthRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserMapper {

    public User requestToUser(AuthRequest request) {
        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .roles(new ArrayList<>())
                .build();
    }
}
