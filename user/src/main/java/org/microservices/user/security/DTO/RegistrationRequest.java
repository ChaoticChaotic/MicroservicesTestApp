package org.microservices.user.security.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    @NotEmpty(message = "Username should not be empty")
    @Size(min = 5, message = "Username is too short!")
    String username;
    @NotEmpty(message = "Password should not be empty")
    @Size(min = 5, message = "Password should be longer than 5 symbols")
    String password;
    @NotEmpty(message = "Password repeat should not be empty")
    String passwordRepeat;
}
