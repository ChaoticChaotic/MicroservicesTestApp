package org.microservices.user.security.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class UserServiceException extends IllegalArgumentException {
    public UserServiceException(String message) {
        super(message);
    }
}
