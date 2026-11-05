package com.piggymetrics.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.piggymetrics.account.domain.*;
import com.piggymetrics.account.service.AccountService;
import com.sun.security.auth.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountControllerTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	@InjectMocks
	private AccountController accountController;

	@Mock
	private AccountService accountService;

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		openMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
	}

	@Test
	public void shouldGetAccountByName() throws Exception {

		final Account account = new Account();
		account.setName("test");

		when(accountService.findByName(account.getName())).thenReturn(account);

		mockMvc.perform(get("/" + account.getName()))
				.andExpect(jsonPath("$.name").value(account.getName()))
				.andExpect(status().isOk());
	}

	@Test
	public void shouldGetCurrentAccount() throws Exception {

		final Account account = new Account();
		account.setName("test");

		when(accountService.findByName(account.getName())).thenReturn(account);

		mockMvc.perform(get("/current").principal(new UserPrincipal(account.getName())))
				.andExpect(jsonPath("$.name").value(account.getName()))
				.andExpect(status().isOk());
	}

	@Test
	public void shouldSaveCurrentAccount() throws Exception {

		Saving saving = new Saving();
		saving.setAmount(new BigDecimal(1500));
		saving.setCurrency(Currency.USD);
		saving.setInterest(new BigDecimal("3.32"));
		saving.setDeposit(true);
		saving.setCapitalization(false);

		Item grocery = new Item();
		grocery.setTitle("Grocery");
		grocery.setAmount(new BigDecimal(10));
		grocery.setCurrency(Currency.USD);
		grocery.setPeriod(TimePeriod.DAY);
		grocery.setIcon("meal");

		Item salary = new Item();
		salary.setTitle("Salary");
		salary.setAmount(new BigDecimal(9100));
		salary.setCurrency(Currency.USD);
		salary.setPeriod(TimePeriod.MONTH);
		salary.setIcon("wallet");

		final Account account = new Account();
		account.setName("test");
		account.setNote("test note");
		account.setLastSeen(new Date());
		account.setSaving(saving);
		account.setExpenses(ImmutableList.of(grocery));
		account.setIncomes(ImmutableList.of(salary));

		String json = mapper.writeValueAsString(account);

		mockMvc.perform(put("/current").principal(new UserPrincipal(account.getName())).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	public void shouldFailOnValidationTryingToSaveCurrentAccount() throws Exception {

		final Account account = new Account();
		account.setName("test");

		String json = mapper.writeValueAsString(account);

		mockMvc.perform(put("/current").principal(new UserPrincipal(account.getName())).contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void shouldRegisterNewAccount() throws Exception {

		mockMvc.perform(post("/").principal(new UserPrincipal("test")))
				.andExpect(status().isOk());
		org.mockito.Mockito.verify(accountService).create("test");
	}

	@Test
	public void shouldIgnoreUserSuppliedNameWhenInitializingAccount() throws Exception {
		mockMvc.perform(post("/").principal(new UserPrincipal("owner"))
				.contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"victim\"}"))
				.andExpect(status().isOk());
		org.mockito.Mockito.verify(accountService).create("owner");
	}
}
