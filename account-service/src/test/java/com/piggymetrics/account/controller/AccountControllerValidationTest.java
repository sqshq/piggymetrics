/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.piggymetrics.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.Item;
import com.piggymetrics.account.service.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountControllerValidationTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	@InjectMocks
	private AccountController accountController;

	@Mock
	private AccountService accountService;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
	}

	@Test
	public void shouldFailOnNegativeAmountWhenCreatingAccount() throws Exception {
		Account account = new Account();
		Item item = new Item();
		item.setAmount(new BigDecimal(-10));
		account.setExpenses(List.of(item));

		String json = mapper.writeValueAsString(account);

		mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void shouldFailOnNegativeAmountWhenUpdatingAccount() throws Exception {
		Account account = new Account();
		Item item = new Item();
		item.setAmount(new BigDecimal(-5));
		account.setExpenses(List.of(item));

		String json = mapper.writeValueAsString(account);

		mockMvc.perform(put("/current").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void shouldAcceptZeroAmountWhenCreatingAccount() throws Exception {
		Account account = new Account();
		Item item = new Item();
		item.setAmount(new BigDecimal(0));
		account.setExpenses(List.of(item));

		String json = mapper.writeValueAsString(account);

		mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isOk());
	}

	@Test
	public void shouldAcceptPositiveAmountWhenCreatingAccount() throws Exception {
		Account account = new Account();
		Item item = new Item();
		item.setAmount(new BigDecimal(10));
		account.setExpenses(List.of(item));

		String json = mapper.writeValueAsString(account);

		mockMvc.perform(post("/").contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isOk());
	}
}