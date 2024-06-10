package org.balaur.financemanagement.exception.user;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PasswordDidNotMatchException extends RuntimeException {
    private final String message;

    public PasswordDidNotMatchException(String message) {
        super(message);
        this.message = message;
    }
}
