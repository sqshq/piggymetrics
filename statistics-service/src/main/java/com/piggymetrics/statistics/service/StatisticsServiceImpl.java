package com.piggymetrics.statistics.service;

import com.piggymetrics.statistics.domain.Account;
import com.piggymetrics.statistics.domain.Currency;
import com.piggymetrics.statistics.domain.Item;
import com.piggymetrics.statistics.domain.timeseries.DataPoint;
import com.piggymetrics.statistics.domain.timeseries.DataPointId;
import com.piggymetrics.statistics.domain.timeseries.ItemMetric;
import com.piggymetrics.statistics.domain.timeseries.StatisticMetric;
import com.piggymetrics.statistics.repository.DataPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StatisticsServiceImpl implements StatisticsService {

	@Autowired
	private DataPointRepository repository;

	@Autowired
	private ExchangeRatesService rateService;

	@Override
	public List<DataPoint> findByAccountName(String accountName) {
		return repository.findByIdAccount(accountName);
	}

	/**
	 * From {@link Account} to {@link DataPoint}
	 *
	 * @param accountName
	 * @param account
	 */
	@Override
	public void save(String accountName, Account account) {

		Instant instant = LocalDate.now().atStartOfDay()
				.atZone(ZoneId.systemDefault()).toInstant();

		DataPointId pointId = new DataPointId(account.getName(), Date.from(instant));

		Set<ItemMetric> incomes = account.getIncomes().stream()
				.map(this::createItemMetric)
				.collect(Collectors.toSet());

		Set<ItemMetric> expenses = account.getIncomes().stream()
				.map(this::createItemMetric)
				.collect(Collectors.toSet());

		Set<StatisticMetric> statistics = createStatisticMetrics(account);

		DataPoint dataPoint = new DataPoint();
		dataPoint.setId(pointId);
		dataPoint.setIncomes(incomes);
		dataPoint.setExpenses(expenses);
		dataPoint.setStatistics(statistics);
		dataPoint.setRates(rateService.getCurrentRates());

		repository.save(dataPoint);
	}

	private ItemMetric createItemMetric(Item item) {

		BigDecimal amount = rateService.convert(item.getCurrency(), Currency.USD, item.getAmount());

		ItemMetric metric = new ItemMetric();
		metric.setTitle(item.getTitle());
		metric.setAmount(amount);

		return metric;
	}

	private Set<StatisticMetric> createStatisticMetrics(Account account) {

		StatisticMetric metric = new StatisticMetric();

		return Stream.of(metric)
				.collect(Collectors.toSet());
	}
}
