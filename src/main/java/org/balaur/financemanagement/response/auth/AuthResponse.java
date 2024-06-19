package org.balaur.financemanagement.response.auth;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Builder
@Getter
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private String token;
    private List<String> userGroupCodes;
    private String currencyCode;
}
