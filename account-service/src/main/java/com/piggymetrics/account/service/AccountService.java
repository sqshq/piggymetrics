package com.piggymetrics.account.service;

import com.piggymetrics.account.domain.Account;

public interface AccountService {

	/**
	 * Finds account by given name
	 *
	 * @param accountName
	 * @return found account
	 */
	Account findByName(String accountName);

	/**
	 * Initializes the authenticated Cognito user's account, idempotently.
	 *
	 * @param username
	 * @return created account
	 */
	Account create(String username);

	/**
	 * Validates and applies incoming account updates
	 * Invokes Statistics Service update
	 *
	 * @param name
	 * @param update
	 */
	void saveChanges(String name, Account update);
}
