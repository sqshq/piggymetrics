package com.piggymetrics.statistics.client;

import com.piggymetrics.statistics.domain.Currency;
import com.piggymetrics.statistics.domain.ExchangeRatesContainer;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ExchangeRatesClient {
    private final RestClient client;
    private final String url;

    public ExchangeRatesClient(RestClient.Builder builder, @Value("${rates.url}") String url) {
        this.client = builder.clone().build();
        this.url = url;
    }

    public ExchangeRatesContainer getRates(Currency base) {
        try {
            ExchangeRatesContainer result = client.get().uri(url, base.name())
                    .retrieve().body(ExchangeRatesContainer.class);
            if (result == null || result.getRates() == null) {
                throw new IllegalArgumentException("Missing rates");
            }
            for (Currency currency : Currency.values()) {
                BigDecimal rate = result.getRates().get(currency.name());
                if (rate == null || rate.signum() <= 0) {
                    throw new IllegalArgumentException("Missing or invalid rate");
                }
            }
            if (result.getRates().get(base.name()).compareTo(BigDecimal.ONE) != 0) {
                throw new IllegalArgumentException("Unexpected base currency");
            }
            result.setBase(base);
            return result;
        } catch (RestClientException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Exchange rates unavailable");
        }
    }
}
