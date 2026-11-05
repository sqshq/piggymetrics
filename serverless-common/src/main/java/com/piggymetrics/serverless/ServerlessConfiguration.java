package com.piggymetrics.serverless;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/** Beans shared by HTTP and scheduled (non-web) Lambda entry points. */
@Configuration
@Import(WebSecurityConfiguration.class)
public class ServerlessConfiguration {

    @Bean
    RestClient.Builder restClientBuilder() {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(
                java.net.http.HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build());
        factory.setReadTimeout(Duration.ofSeconds(20));
        return RestClient.builder().requestFactory(factory);
    }

    @Bean
    ServiceTokenProvider serviceTokenProvider(RestClient.Builder builder,
                                              @Value("${cognito.token-uri}") String tokenUri,
                                              @Value("${cognito.service-client-id}") String clientId,
                                              @Value("${cognito.service-client-secret}") String clientSecret,
                                              @Value("${cognito.service-scope}") String scope) {
        return new CognitoServiceTokenProvider(builder.clone().build(), tokenUri, clientId, clientSecret, scope);
    }
}
