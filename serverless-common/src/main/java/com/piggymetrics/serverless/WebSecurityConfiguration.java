package com.piggymetrics.serverless;

import java.util.Collection;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnWebApplication
@EnableMethodSecurity
public class WebSecurityConfiguration {

    /** Scope Cognito puts on user-pool sign-in access tokens, never on client-credentials tokens. */
    public static final String USER_AUTHORITY = "SCOPE_aws.cognito.signin.user.admin";

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/demo", "/rates").permitAll()
                        .requestMatchers("/current", "/", "/recipients/**").hasAuthority(USER_AUTHORITY)
                        .anyRequest().authenticated())
                .oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.jwtAuthenticationConverter(token -> {
                    Collection<GrantedAuthority> authorities = scopes.convert(token);
                    // User tokens carry "username"; client-credentials tokens only carry sub (the client id).
                    String name = token.hasClaim("username") ? token.getClaimAsString("username") : token.getSubject();
                    return new JwtAuthenticationToken(token, authorities, name);
                })))
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${cognito.issuer}") String issuer,
                          @Value("${cognito.browser-client-id}") String browserClient,
                          @Value("${cognito.service-client-id}") String serviceClient) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(issuer + "/.well-known/jwks.json").build();
        Set<String> clients = Set.of(browserClient, serviceClient);
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer),
                jwt -> "access".equals(jwt.getClaimAsString("token_use"))
                        && clients.contains(jwt.getClaimAsString("client_id"))
                        ? OAuth2TokenValidatorResult.success()
                        : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"))));
        return decoder;
    }
}
