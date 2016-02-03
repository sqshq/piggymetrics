package com.piggymetrics.service;

import com.piggymetrics.client.StatisticsClient;
import com.piggymetrics.domain.Account;
import com.piggymetrics.domain.User;
import com.piggymetrics.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository repository;

	@Autowired
	private StatisticsClient statisticsClient;

	@Override
	public Account findByName(String name) {
		return repository.findByName(name);
	}

	@Override
	public void saveChanges(String accountName, Account account) {

		// get account by name
		// set changes
		// logic

	}

	@Override
	public Account create(User user) {

		// create user
		// create account
		// create notifications

		return null;
	}
}
