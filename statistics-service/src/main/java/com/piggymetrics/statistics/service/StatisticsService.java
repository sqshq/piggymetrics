package com.piggymetrics.statistics.service;

import com.piggymetrics.statistics.domain.Account;
import com.piggymetrics.statistics.domain.timeseries.DataPoint;

import java.util.List;

public interface StatisticsService {

	List<DataPoint> findByAccountName(String accountName);

	void save(String accountName, Account account);

}
