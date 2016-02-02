package com.piggymetrics.controller;

import com.piggymetrics.domain.Account;
import com.piggymetrics.domain.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class AccountController {

	//@PreAuthorize("#oauth2.hasScope('server')")
	@RequestMapping(path = "/{accountId}", method = RequestMethod.GET)
	public Account getAccountById(@PathVariable String accountId) {
		return null;
	}

	@RequestMapping(path = "/current", method = RequestMethod.GET)
	public Account getCurrentAccount(Principal principal) {
		return null;
	}

	@RequestMapping(path = "/current", method = RequestMethod.PUT)
	public void saveCurrentAccount(Principal principal) {

	}

	@RequestMapping(path = "/demo", method = RequestMethod.GET)
	public Account getDemoAccount() {
		return null;
	}

	@RequestMapping(path = "/registration", method = RequestMethod.POST)
	public Account createNewAccount(User user) {
		return new Account();
	}
}
