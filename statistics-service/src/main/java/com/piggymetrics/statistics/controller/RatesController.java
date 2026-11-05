package com.piggymetrics.statistics.controller;

import com.piggymetrics.statistics.domain.Currency;
import com.piggymetrics.statistics.service.ExchangeRatesService;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RatesController {
    private final ExchangeRatesService rates;
    public RatesController(ExchangeRatesService rates) {
        this.rates = rates;
    }
    @GetMapping("/rates")
    public Map<Currency, BigDecimal> currentRates() {
        return rates.getCurrentRates();
    }
}
