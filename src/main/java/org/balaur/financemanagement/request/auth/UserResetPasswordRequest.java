package org.balaur.financemanagement.request.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResetPasswordRequest {
    @NotNull(message = "Password field can't be missing from request")
    @NotEmpty(message = "Password field can't be empty")
    private String password;
    @NotNull(message = "Confirm password field can't be missing from request")
    @NotEmpty(message = "Confirm password field can't be empty")
    private String confirmPassword;
    @NotNull(message = "Token field can't be missing from request")
    @NotEmpty(message = "Token field can't be empty")
    private String token;
}
