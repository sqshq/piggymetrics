package com.piggymetrics.account.controller;

import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.User;
import com.piggymetrics.account.service.AccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import javax.validation.Valid;
import java.security.Principal;

@RestController
public class AccountController {

  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @PreAuthorize("#oauth2.hasScope('server') or #name.equals('demo')")
  @GetMapping(
      path = "/{name}",
      consumes = APPLICATION_JSON_VALUE,
      produces = APPLICATION_JSON_VALUE)
  public Account getAccountByName(@PathVariable String name) {
    return accountService.findByName(name);
  }

  @GetMapping(path = "/current")
  public Account getCurrentAccount(Principal principal) {
    return accountService.findByName(principal.getName());
  }

  @PutMapping(path = "/current")
  public void saveCurrentAccount(Principal principal, @Valid @RequestBody Account account) {
    accountService.saveChanges(principal.getName(), account);
  }

  @PostMapping(path = "/")
  public Account createNewAccount(@Valid @RequestBody User user) {
    return accountService.create(user);
  }
}
