package com.piggymetrics.account.service;

import com.piggymetrics.account.client.AuthServiceClient;
import com.piggymetrics.account.client.StatisticsServiceClient;
import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.Currency;
import com.piggymetrics.account.domain.Saving;
import com.piggymetrics.account.domain.User;
import com.piggymetrics.account.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService {

	private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

	@Autowired
	private StatisticsServiceClient statisticsClient;

	@Autowired
	private AuthServiceClient authClient;

	@Autowired
	private AccountRepository repository;

	@Override
	public Account findByName(String name) {
		Assert.hasLength(name);
		return repository.findByName(name);
	}

	/**
	 * Checks if account with the same name already exists
	 * Invokes Auth Service user creation
	 * Creates new account with default parameters
	 *
	 * @param user
	 * @return created account
	 */
	@Override
	public Account create(User user) {

		Account existing = repository.findByName(user.getUsername());
		Assert.isNull(existing, "account already exists: " + user.getUsername());

		authClient.createUser(user);

		Saving saving = new Saving();
		saving.setAmount(new BigDecimal(0));
		saving.setCurrency(Currency.getDefault());
		saving.setInterest(new BigDecimal(0));
		saving.setDeposit(false);
		saving.setCapitalization(false);

		Account account = new Account();
		account.setName(user.getUsername());
		account.setLastSeen(new Date());
		account.setSaving(saving);

		repository.save(account);

		log.info("new account has been created: " + account.getName());

		return account;
	}

	/**
	 * Validates and applies incoming account updates
	 * Invokes Statistics Service update
	 *
	 * @param name
	 * @param update
	 */
	@Override
	public void saveChanges(String name, Account update) {

		Assert.notEmpty(update.getExpenses());
		Assert.notEmpty(update.getIncomes());

		Account account = repository.findByName(name);
		Assert.notNull(account, "can't find account with name " + name);

		account.setIncomes(update.getIncomes());
		account.setExpenses(update.getExpenses());
		account.setSaving(update.getSaving());
		account.setNote(update.getNote());
		account.setLastSeen(new Date());

		repository.save(account);

		try {
			statisticsClient.updateStatistics(name, account);
		} catch (Exception e) {
			log.error("error during statistics update", e);
		}
	}
}
