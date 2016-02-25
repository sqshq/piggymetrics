package com.piggymetrics.statistics.service;

import com.piggymetrics.statistics.StatisticsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StatisticsApplication.class)
public class ExchangeRatesServiceImplTest {

	@Autowired
	private ExchangeRatesService rateService;

	@Test
	public void test() {
		rateService.getCurrentRates();
	}
}