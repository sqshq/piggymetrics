package com.piggymetrics.account.client;

import com.piggymetrics.account.domain.Account;
import com.piggymetrics.serverless.ServiceTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class StatisticsServiceClient {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(StatisticsServiceClient.class);
    private final RestClient client;
    private final ServiceTokenProvider tokens;

    public StatisticsServiceClient(RestClient.Builder builder, ServiceTokenProvider tokens,
                                   @Value("${services.base-url}") String baseUrl) {
        this.client = builder.clone().baseUrl(baseUrl).build();
        this.tokens = tokens;
    }

    /** Best effort, like the former Hystrix fallback: the account change is already saved. */
    public void updateStatistics(String accountName, Account account) {
        try {
            client.put().uri("/statistics/{name}", accountName)
                    .headers(headers -> headers.setBearerAuth(tokens.getToken()))
                    .contentType(MediaType.APPLICATION_JSON).body(account).retrieve().toBodilessEntity();
        } catch (RuntimeException exception) {
            LOGGER.error("Error during update statistics for account: {} ({})", accountName,
                    exception.getClass().getSimpleName());
        }
    }
}
