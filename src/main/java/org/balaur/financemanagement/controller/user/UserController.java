package org.balaur.financemanagement.controller.user;

import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/preferred-currency")
    public ResponseEntity<String> getPreferredCurrency(Authentication authentication) {
        return userService.getPreferredCurrency(authentication);
    }
}
