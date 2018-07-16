package com.piggymetrics.statistics.client;

import com.piggymetrics.statistics.domain.Currency;
import com.piggymetrics.statistics.domain.ExchangeRatesContainer;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Collection;

@FeignClient(url = "${rates.url}", name = "rates-client", fallback = ExchangeRatesClientFallback.class)
public interface ExchangeRatesClient {

	default ExchangeRatesContainer getRates(Currency base) {
		return getRates(base, Arrays.asList(Currency.values()));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/latest")
	ExchangeRatesContainer getRates(
			@RequestParam("base") Currency base,
			@RequestParam("symbols") Collection<Currency> currencies);

}
