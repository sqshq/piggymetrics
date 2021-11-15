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
	@GetMapping("/{name}")
	public Account getAccountByName(@PathVariable String name) {
		return accountService.findByName(name);
	}

	@GetMapping("/current")
	public Account getCurrentAccount(Principal principal) {
		return accountService.findByName(principal.getName());
	}

	@PutMapping("/current")
	public void saveCurrentAccount(Principal principal, @Valid @RequestBody Account account) {
		accountService.saveChanges(principal.getName(), account);
	}

	@PostMapping("/")
	public Account createNewAccount(@Valid @RequestBody User user) {
		return accountService.create(user);
	}
}
