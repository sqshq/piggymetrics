package com.piggymetrics.statistics;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@org.springframework.context.annotation.Import(com.piggymetrics.serverless.MongoTestConfiguration.class)
public class StatisticsServiceApplicationTests {

	@Test
	public void contextLoads() {
	}

}
