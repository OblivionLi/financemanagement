package org.balaur.financemanagement.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {
    @NotNull(message = "Email field can't be missing from request")
    @NotEmpty(message = "Email field can't be empty")
    @Email(message = "Email field must be of valid format")
    private String email;
    @NotNull(message = "Password field can't be missing from request")
    @NotEmpty(message = "Password field can't be empty")
    private String password;
}
