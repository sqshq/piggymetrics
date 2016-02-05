package com.piggymetrics.repository;

import com.piggymetrics.AccountApplication;
import com.piggymetrics.domain.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AccountApplication.class)
public class AccountRepositoryTest {

	@Autowired
	private AccountRepository repository;

	@Test
	public void shouldRetrieveAccountByDemoUser() {

		Account account = new Account();
		account.setName("demo");
		account.setNote("my note!");
		repository.save(account);

		Account found = repository.findByName("demo");
		System.out.println(found);
	}
}
