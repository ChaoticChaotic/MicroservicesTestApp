package org.microservices.user.service.registration;

import org.microservices.user.security.DTO.RegistrationRequest;

public interface RegistrationService {
    void register(RegistrationRequest request);
}
