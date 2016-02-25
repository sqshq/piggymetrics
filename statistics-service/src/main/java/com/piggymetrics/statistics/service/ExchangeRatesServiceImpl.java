package com.piggymetrics.statistics.service;

import com.piggymetrics.statistics.client.ExchangeRatesClient;
import com.piggymetrics.statistics.domain.Currency;
import com.piggymetrics.statistics.domain.ExchangeRatesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

	private static final Logger log = LoggerFactory.getLogger(ExchangeRatesServiceImpl.class);

	private ExchangeRatesContainer container;

	@Autowired
	private ExchangeRatesClient client;

	@Override
	public Map<Currency, BigDecimal> getCurrentRates() {

		if (container == null || !container.getDate().equals(LocalDate.now())) {
			container = client.getRates(Currency.getBase());
			log.info("exchange rates has been updated: {}", container);
		}

		return new HashMap<Currency, BigDecimal>() {{
			put(Currency.EUR, container.getRates().get(Currency.EUR.name()));
			put(Currency.RUB, container.getRates().get(Currency.RUB.name()));
			put(Currency.USD, BigDecimal.ONE);
		}};
	}

	@Override
	public BigDecimal convert(Currency from, Currency to, BigDecimal amount) {

		Map<Currency, BigDecimal> rates = getCurrentRates();
		BigDecimal ratio = rates.get(to).divide(rates.get(from), 4, RoundingMode.HALF_UP);

		return amount.multiply(ratio);
	}
}
