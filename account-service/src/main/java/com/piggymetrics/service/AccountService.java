package com.piggymetrics.service;

import com.piggymetrics.domain.Account;
import com.piggymetrics.domain.User;

public interface AccountService {

	Account create(User user);

	Account findByName(String name);

	void saveChanges(String name, Account update);
}
