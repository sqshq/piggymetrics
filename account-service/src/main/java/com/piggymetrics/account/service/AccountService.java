package com.piggymetrics.account.service;

import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.User;

public interface AccountService {

	Account create(User user);

	Account findByName(String name);

	void saveChanges(String name, Account update);
}
