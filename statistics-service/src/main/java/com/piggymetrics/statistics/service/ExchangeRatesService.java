package com.piggymetrics.statistics.service;

import com.piggymetrics.statistics.domain.Currency;

import java.math.BigDecimal;
import java.util.Map;

public interface ExchangeRatesService {

	Map<Currency, BigDecimal> getCurrentRates();

	BigDecimal convert(Currency from, Currency to, BigDecimal amount);
}
