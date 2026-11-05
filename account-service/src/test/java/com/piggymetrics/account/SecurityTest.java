package com.piggymetrics.account;

import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.service.AccountService;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Exercises the real filter chain and JWT-to-principal mapping with Cognito-shaped tokens. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(com.piggymetrics.serverless.MongoTestConfiguration.class)
class SecurityTest {
	@Autowired MockMvc mvc;
	@MockitoBean JwtDecoder decoder;
	@MockitoBean AccountService accounts;

	@BeforeEach
	void tokens() {
		when(decoder.decode("user")).thenReturn(jwt(Map.of("sub", "uuid-1", "username", "alice",
				"scope", "aws.cognito.signin.user.admin", "client_id", "browser")));
		when(decoder.decode("machine")).thenReturn(jwt(Map.of("sub", "service-client",
				"scope", "piggymetrics/server", "client_id", "service-client")));
		when(accounts.findByName(anyString())).thenAnswer(call -> {
			Account account = new Account();
			account.setName(call.getArgument(0));
			return account;
		});
	}

	private static Jwt jwt(Map<String, Object> claims) {
		return Jwt.withTokenValue("t").header("alg", "RS256").claims(c -> c.putAll(claims))
				.issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300)).build();
	}

	@Test
	void userTokenResolvesCognitoUsername() throws Exception {
		mvc.perform(get("/current").header("Authorization", "Bearer user"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("alice"));
	}

	@Test
	void accountInitializationUsesTokenIdentity() throws Exception {
		mvc.perform(post("/current").header("Authorization", "Bearer user")).andExpect(status().isOk());
		verify(accounts).create("alice");
	}

	@Test
	void machineTokenCannotActAsCurrentUser() throws Exception {
		mvc.perform(get("/current").header("Authorization", "Bearer machine")).andExpect(status().isForbidden());
		mvc.perform(post("/").header("Authorization", "Bearer machine")).andExpect(status().isForbidden());
	}

	@Test
	void onlyServiceScopeReadsOtherAccounts() throws Exception {
		mvc.perform(get("/bob").header("Authorization", "Bearer machine")).andExpect(status().isOk());
		mvc.perform(get("/bob").header("Authorization", "Bearer user")).andExpect(status().isForbidden());
	}

	@Test
	void anonymousAccess() throws Exception {
		mvc.perform(get("/demo")).andExpect(status().isOk());
		mvc.perform(get("/current")).andExpect(status().isUnauthorized());
		mvc.perform(post("/")).andExpect(status().isUnauthorized());
		verify(accounts, never()).create(anyString());
	}
}
