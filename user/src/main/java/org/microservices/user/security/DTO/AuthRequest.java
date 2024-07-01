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
public class AuthRequest {
    @NotEmpty(message = "authRequest.loginEmpty")
    @Size(min = 5, message = "authRequest.loginIsTooShort")
    String username;
    @NotEmpty(message = "authRequest.passwordEmpty")
    @Size(min = 5, message = "authRequest.passwordIsTooShort")
    String password;
    @NotEmpty(message = "authRequest.passwordEmpty")
    @Size(min = 5, message = "authRequest.passwordIsTooShort")
    String passwordRepeat;
}
