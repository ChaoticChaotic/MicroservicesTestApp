package org.microservices.user.service.registration;

import org.microservices.user.security.DTO.AuthRequest;

public interface RegistrationService {
    void register(AuthRequest request);
}
