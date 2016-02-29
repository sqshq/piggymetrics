package com.piggymetrics.statistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.piggymetrics.statistics.StatisticsApplication;
import com.piggymetrics.statistics.domain.*;
import com.piggymetrics.statistics.domain.timeseries.DataPoint;
import com.piggymetrics.statistics.domain.timeseries.DataPointId;
import com.piggymetrics.statistics.service.StatisticsService;
import com.sun.security.auth.UserPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StatisticsApplication.class)
@WebAppConfiguration
public class StatisticsControllerTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	@InjectMocks
	private StatisticsController statisticsController;

	@Mock
	private StatisticsService statisticsService;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
	}

	@Test
	public void shouldGetStatisticsByAccountName() throws Exception {

		final DataPoint dataPoint = new DataPoint();
		dataPoint.setId(new DataPointId("test", new Date()));

		when(statisticsService.findByAccountName(dataPoint.getId().getAccount()))
				.thenReturn(ImmutableList.of(dataPoint));

		mockMvc.perform(get("/test").principal(new UserPrincipal(dataPoint.getId().getAccount())))
				.andExpect(jsonPath("$[0].id.account").value(dataPoint.getId().getAccount()))
				.andExpect(status().isOk());
	}

	@Test
	public void shouldGetCurrentAccountStatistics() throws Exception {

		final DataPoint dataPoint = new DataPoint();
		dataPoint.setId(new DataPointId("test", new Date()));

		when(statisticsService.findByAccountName(dataPoint.getId().getAccount()))
				.thenReturn(ImmutableList.of(dataPoint));

		mockMvc.perform(get("/current").principal(new UserPrincipal(dataPoint.getId().getAccount())))
				.andExpect(jsonPath("$[0].id.account").value(dataPoint.getId().getAccount()))
				.andExpect(status().isOk());
	}

	@Test
	public void shouldSaveAccountStatistics() throws Exception {

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

		Item salary = new Item();
		salary.setTitle("Salary");
		salary.setAmount(new BigDecimal(9100));
		salary.setCurrency(Currency.USD);
		salary.setPeriod(TimePeriod.MONTH);

		final Account account = new Account();
		account.setSaving(saving);
		account.setExpenses(ImmutableList.of(grocery));
		account.setIncomes(ImmutableList.of(salary));

		String json = mapper.writeValueAsString(account);

		mockMvc.perform(put("/test").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());

		verify(statisticsService, times(1)).save(anyString(), any(Account.class));
	}
}