package com.piggymetrics.notification.client;

import com.piggymetrics.serverless.ServiceTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AccountServiceClient {
    private final RestClient client;
    private final ServiceTokenProvider tokens;

    public AccountServiceClient(RestClient.Builder builder, ServiceTokenProvider tokens,
                               @Value("${services.base-url}") String baseUrl) {
        this.client = builder.clone().baseUrl(baseUrl).build();
        this.tokens = tokens;
    }

    public String getAccount(String accountName) {
        return client.get().uri("/accounts/{name}", accountName)
                .headers(headers -> headers.setBearerAuth(tokens.getToken()))
                .retrieve().body(String.class);
    }
}
