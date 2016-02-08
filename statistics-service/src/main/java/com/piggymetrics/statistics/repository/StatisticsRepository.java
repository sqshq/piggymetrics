package com.piggymetrics.statistics.repository;

import com.piggymetrics.statistics.domain.Statistics;

public interface StatisticsRepository {

	Statistics findByAccountName(String accountName);

}
