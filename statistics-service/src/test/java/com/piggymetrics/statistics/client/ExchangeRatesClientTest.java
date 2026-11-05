package com.piggymetrics.statistics.client;

import com.piggymetrics.statistics.domain.Currency;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ExchangeRatesClientTest {
    MockRestServiceServer server;
    ExchangeRatesClient client;

    @BeforeEach
    void setup() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new ExchangeRatesClient(builder, "https://rates.test/latest/{base}");
    }

    @Test
    void retrievesRatesForRequestedBase() {
        server.expect(requestTo("https://rates.test/latest/USD")).andRespond(withSuccess(
                "{\"rates\":{\"USD\":1,\"EUR\":0.8,\"RUB\":80}}", MediaType.APPLICATION_JSON));
        var rates = client.getRates(Currency.USD);
        assertEquals(Currency.USD, rates.getBase());
        assertEquals(new BigDecimal("80"), rates.getRates().get("RUB"));
        server.verify();
    }

    @Test
    void supportsAnotherBase() {
        server.expect(requestTo("https://rates.test/latest/EUR")).andRespond(withSuccess(
                "{\"rates\":{\"USD\":1.25,\"EUR\":1,\"RUB\":100}}", MediaType.APPLICATION_JSON));
        assertEquals(Currency.EUR, client.getRates(Currency.EUR).getBase());
        server.verify();
    }

    @Test
    void rejectsMissingRatesRatherThanInventingFinancialData() {
        server.expect(requestTo("https://rates.test/latest/USD"))
                .andRespond(withSuccess("{\"rates\":{}}", MediaType.APPLICATION_JSON));
        assertEquals(HttpStatus.BAD_GATEWAY,
                assertThrows(ResponseStatusException.class, () -> client.getRates(Currency.USD)).getStatusCode());
    }

    @Test
    void reportsProviderFailure() {
        server.expect(requestTo("https://rates.test/latest/USD")).andRespond(withServerError());
        assertThrows(ResponseStatusException.class, () -> client.getRates(Currency.USD));
    }
}
