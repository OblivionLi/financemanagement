package org.balaur.financemanagement.controller.currency;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.request.currency.CurrencyUpdateRequest;
import org.balaur.financemanagement.service.currency.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @PatchMapping("/update")
    public ResponseEntity<String> updateCurrency(Authentication authentication, @Valid @RequestBody CurrencyUpdateRequest request) {
        return currencyService.updateCurrency(authentication, request);
    }
}
