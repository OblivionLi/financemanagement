package org.balaur.financemanagement.exception.user;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(code = HttpStatus.FOUND)
public class UserFoundException extends RuntimeException{
    private final String message;

    public UserFoundException(String message) {
        super(message);
        this.message = message;
    }
}
