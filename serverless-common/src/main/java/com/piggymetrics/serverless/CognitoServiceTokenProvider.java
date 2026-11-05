package com.piggymetrics.serverless;

import java.time.Instant;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

/** Cognito client-credentials token, cached per Lambda execution environment. */
public class CognitoServiceTokenProvider implements ServiceTokenProvider {
    private static final long REFRESH_MARGIN_SECONDS = 60;

    private final RestClient client;
    private final String tokenUri;
    private final String clientId;
    private final String clientSecret;
    private final String scope;
    private String token;
    private Instant expiresAt = Instant.EPOCH;

    public CognitoServiceTokenProvider(RestClient client, String tokenUri, String clientId,
                                       String clientSecret, String scope) {
        this.client = client;
        this.tokenUri = tokenUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
    }

    @Override
    public synchronized String getToken() {
        if (token != null && Instant.now().isBefore(expiresAt.minusSeconds(REFRESH_MARGIN_SECONDS))) {
            return token;
        }
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("scope", scope);
        Map<String, Object> response = client.post().uri(tokenUri)
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        if (response == null || !(response.get("access_token") instanceof String accessToken)) {
            throw new IllegalStateException("Service token unavailable");
        }
        long expiresIn = response.get("expires_in") instanceof Number seconds ? seconds.longValue() : 300;
        token = accessToken;
        expiresAt = Instant.now().plusSeconds(expiresIn);
        return token;
    }
}
