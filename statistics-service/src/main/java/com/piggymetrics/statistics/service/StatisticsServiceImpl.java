package com.piggymetrics.statistics.service;

import com.piggymetrics.statistics.domain.Account;
import com.piggymetrics.statistics.domain.Statistics;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

	@Override
	public Statistics findByAccountName(String accountName) {
		return null;
	}

	@Override
	public void save(String accountName, Account account) {

	}
}
