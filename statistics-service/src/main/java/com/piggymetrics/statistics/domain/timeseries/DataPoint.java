package com.piggymetrics.statistics.domain.timeseries;

import com.piggymetrics.statistics.domain.Currency;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * Represents daily time series data point
 *
 * все приводтся к usd и к периоду в 1 день
 * чтобы можно было любой айтем посмотреть как менялся
 *
 */

@Document(collection = "datapoints")
public class DataPoint {

	@Id
	private DataPointId id;

	private Set<StatisticMetric> statistics;

	private Set<ItemMetric> incomes;

	private Set<ItemMetric> expenses;

	private Map<Currency, BigDecimal> rates;

	public DataPointId getId() {
		return id;
	}

	public void setId(DataPointId id) {
		this.id = id;
	}

	public Set<StatisticMetric> getStatistics() {
		return statistics;
	}

	public void setStatistics(Set<StatisticMetric> statistics) {
		this.statistics = statistics;
	}

	public Set<ItemMetric> getIncomes() {
		return incomes;
	}

	public void setIncomes(Set<ItemMetric> incomes) {
		this.incomes = incomes;
	}

	public Set<ItemMetric> getExpenses() {
		return expenses;
	}

	public void setExpenses(Set<ItemMetric> expenses) {
		this.expenses = expenses;
	}

	public Map<Currency, BigDecimal> getRates() {
		return rates;
	}

	public void setRates(Map<Currency, BigDecimal> rates) {
		this.rates = rates;
	}
}
