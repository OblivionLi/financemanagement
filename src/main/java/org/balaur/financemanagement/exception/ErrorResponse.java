package org.balaur.financemanagement.exception;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.List;

@Builder
public record ErrorResponse(String timestamp,
                            HttpStatus status,
                            int statusCode,
                            String exception,
                            String message,
                            String path,
                            List<String> errors)
{
}
