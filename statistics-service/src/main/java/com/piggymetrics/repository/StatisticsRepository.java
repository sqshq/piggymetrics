package com.piggymetrics.repository;

import com.piggymetrics.domain.Statistics;

public interface StatisticsRepository {

	Statistics findByAccountName(String accountName);

}
