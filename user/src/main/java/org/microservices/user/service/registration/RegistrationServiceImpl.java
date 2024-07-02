package org.microservices.user.service.registration;

import org.microservices.user.model.User;
import org.microservices.user.security.DTO.RegistrationRequest;
import org.microservices.user.security.DTO.mapper.UserMapper;
import org.microservices.user.service.user.UserService;
import org.springframework.stereotype.Service;


@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final String DEFAULT_ROLE = "ROLE_USER";

    private final UserService userService;
    private final UserMapper mapper;

    public RegistrationServiceImpl(UserService userService, UserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public void register(RegistrationRequest request) {
        User user = userService.save(mapper.requestToUser(request));
        userService.addRoleToUser(user.getId(), DEFAULT_ROLE);
    }
}
