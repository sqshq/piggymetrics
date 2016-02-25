package com.piggymetrics.statistics.repository;

import com.google.common.collect.Sets;
import com.piggymetrics.statistics.StatisticsApplication;
import com.piggymetrics.statistics.domain.timeseries.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StatisticsApplication.class)
public class DataPointRepositoryTest {

	@Autowired
	private DataPointRepository repository;

	@Test
	public void shouldSaveDataPoint() {

		DataPoint point1 = getDataPoint(false);
		repository.save(point1);

		DataPoint point2 = getDataPoint(true);
		repository.save(point2);

		List<DataPoint> points = repository.findByIdAccount("sqshq");
		System.out.println(points);
	}

	private DataPoint getDataPoint(boolean many) {

		ItemMetric salary = new ItemMetric();
		salary.setTitle("salary");
		salary.setAmount(new BigDecimal(2000));

		ItemMetric grocery = new ItemMetric();
		grocery.setTitle("grocery");
		grocery.setAmount(new BigDecimal(1000));

		ItemMetric gas = new ItemMetric();
		gas.setTitle("gas");
		gas.setAmount(new BigDecimal(500));

		StatisticMetric savings = new StatisticMetric();
		savings.setType(StatisticType.SAVING_AMOUNT);
		savings.setValue(new BigDecimal(192_000));

		StatisticMetric interest = new StatisticMetric();
		interest.setType(StatisticType.INTEREST_VALUE);
		interest.setValue(new BigDecimal("9.36"));

		DataPoint point = new DataPoint();
		point.setStatistics(Sets.newHashSet(savings, interest));

		if (many) {
			point.setExpenses(Sets.newHashSet(grocery, gas));
		} else {
			point.setExpenses(Sets.newHashSet(grocery));
		}

		point.setIncomes(Sets.newHashSet(salary));

		DataPointId id = new DataPointId("sqshq", new Date(many ? 0 : 1000));
		point.setId(id);

		return point;
	}
}