package com.piggymetrics.account;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@org.springframework.context.annotation.Import(com.piggymetrics.serverless.MongoTestConfiguration.class)
public class AccountServiceApplicationTests {

	@Test
	public void contextLoads() {

	}

}
