package com.piggymetrics.statistics.domain.timeseries;

import java.math.BigDecimal;

public class StatisticMetric {

	private StatisticType type;

	private BigDecimal value;

	public StatisticMetric(StatisticType type, BigDecimal value) {
		this.type = type;
		this.value = value;
	}

	public StatisticType getType() {
		return type;
	}

	public BigDecimal getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StatisticMetric that = (StatisticMetric) o;

		return type == that.type;

	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}
}
