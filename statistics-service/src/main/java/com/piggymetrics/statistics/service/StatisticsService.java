package com.piggymetrics.statistics.service;

import com.piggymetrics.statistics.domain.Account;
import com.piggymetrics.statistics.domain.Statistics;

public interface StatisticsService {

	Statistics findByAccountName(String accountName);

	void save(String accountName, Account account);

}
