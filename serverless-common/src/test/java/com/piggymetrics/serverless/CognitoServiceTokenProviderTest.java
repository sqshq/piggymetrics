package com.piggymetrics.serverless;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CognitoServiceTokenProviderTest {

    @Test
    void requestsClientCredentialsTokenOnceAndCachesIt() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(ExpectedCount.once(), requestTo("https://auth.test/oauth2/token"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Basic aWQ6c2VjcmV0"))
                .andExpect(content().formDataContains(java.util.Map.of(
                        "grant_type", "client_credentials", "scope", "piggymetrics/server")))
                .andRespond(withSuccess("{\"access_token\":\"abc\",\"expires_in\":3600}", MediaType.APPLICATION_JSON));
        var provider = new CognitoServiceTokenProvider(builder.build(), "https://auth.test/oauth2/token",
                "id", "secret", "piggymetrics/server");
        assertEquals("abc", provider.getToken());
        assertEquals("abc", provider.getToken());
        server.verify();
    }

    @Test
    void failsWithoutLeakingResponseWhenTokenMissing() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://auth.test/oauth2/token"))
                .andRespond(withSuccess("{\"error\":\"x\"}", MediaType.APPLICATION_JSON));
        var provider = new CognitoServiceTokenProvider(builder.build(), "https://auth.test/oauth2/token",
                "id", "secret", "s");
        assertEquals("Service token unavailable",
                assertThrows(IllegalStateException.class, provider::getToken).getMessage());
    }
}
