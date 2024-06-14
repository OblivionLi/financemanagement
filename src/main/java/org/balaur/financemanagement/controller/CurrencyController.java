package org.balaur.financemanagement.controller;

import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.response.CurrencyResponse;
import org.balaur.financemanagement.service.currency.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
