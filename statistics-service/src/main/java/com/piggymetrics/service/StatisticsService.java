package com.piggymetrics.service;

import com.piggymetrics.domain.Account;
import com.piggymetrics.domain.Statistics;

public interface StatisticsService {

	Statistics findByAccountName(String accountName);

	void save(String accountName, Account account);

}
