package org.microservices.user.security.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePassRequest {
    @NotEmpty(message = "changePassRequest.oldPassEmpty")
    @Size(min = 5, message = "changePassRequest.oldPassIsTooShort")
    String oldPassword;
    @NotEmpty(message = "changePassRequest.newPassEmpty")
    @Size(min = 5, message = "changePassRequest.newPassIsTooShort")
    String newPassword;
    @NotEmpty(message = "changePassRequest.confirmationEmpty")
    @Size(min = 5, message = "changePassRequest.confirmationIsTooShort")
    String newPasswordConfirmation;
}
