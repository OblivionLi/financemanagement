package org.balaur.financemanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.response.CurrencyResponse;
import org.balaur.financemanagement.service.currency.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping()
    public ResponseEntity<List<CurrencyResponse>> getCurrencies() {
        return currencyService.getCurrencies();
    }

//    @PostMapping()
//    public ResponseEntity<String> setAccountCurrency(@Valid @RequestBody CurrencyCreateRequest currencyCreateRequest) {
//        return currencyService.setAccountCurrency();
//    }
}
