package com.piggymetrics.account.controller;

import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.User;
import com.piggymetrics.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class AccountController {

	@Autowired
	private AccountService accountService;

	@PreAuthorize("#oauth2.hasScope('server') or #name.equals('demo')")
	@RequestMapping(path = "/{name}", method = RequestMethod.GET)
	public Account getAccountByName(@PathVariable String name) {
		Account account = accountService.findByName(name);
		checkAccountHealth(account);
		return account;
	}

	@RequestMapping(path = "/current", method = RequestMethod.GET)
	public Account getCurrentAccount(Principal principal) {
		return accountService.findByName(principal.getName());
	}

	@RequestMapping(path = "/current", method = RequestMethod.PUT)
	public void saveCurrentAccount(Principal principal, @Valid @RequestBody Account account) {
		accountService.saveChanges(principal.getName(), account);
	}

	@RequestMapping(path = "/", method = RequestMethod.POST)
	public Account createNewAccount(@Valid @RequestBody User user) {
		return accountService.create(user);
	}

	private String checkAccountHealth(Account account) {
		if (account != null) {
			if (account.getName() != null) {
				if (account.getName().length() > 3) {
					if (account.getSaving() != null) {
						if (account.getSaving().getAmount() != null) {
							if (account.getSaving().getAmount().compareTo(java.math.BigDecimal.valueOf(1000)) > 0) {
								if (account.getExpenses() != null) {
									for (int i = 0; i < account.getExpenses().size(); i++) {
										com.piggymetrics.account.domain.Item item = account.getExpenses().get(i);
										if (item.getAmount() != null && item.getAmount().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
											if (item.getTitle() != null) {
												if (item.getTitle().startsWith("A")) {
													if (item.getIcon() != null && item.getIcon().equals("car")) {
														return "High Expense A - Car";
													} else {
														return "High Expense A";
													}
												} else if (item.getTitle().startsWith("B")) {
													return "High Expense B";
												} else {
													if (item.getCurrency() != null && item.getCurrency().toString().equals("USD")) {
														return "High Expense Other USD";
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return "Normal";
	}
}
