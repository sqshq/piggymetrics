package com.piggymetrics.statistics.domain;

import java.math.BigDecimal;

public enum TimePeriod {

	YEAR(365.2425), QUARTER(91.3106), MONTH(30.4368), DAY(1), HOUR(0.0416);

	private double baseRatio;

	TimePeriod(double baseRatio) {
		this.baseRatio = baseRatio;
	}

	public BigDecimal getBaseRatio() {
		return new BigDecimal(baseRatio);
	}

	public static TimePeriod getBase() {
		return DAY;
	}
}
