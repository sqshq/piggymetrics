package com.piggymetrics.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class StatisticsController {

	//@PreAuthorize("#oauth2.hasScope('server')")
	@RequestMapping(value = "/{accountId}", method = RequestMethod.GET)
	public String getByAccountId(@PathVariable String accountId) {
		return "get by accound id " + accountId;
	}

	//@PreAuthorize("#oauth2.hasScope('server')")
	@RequestMapping(value = "/{accountId}", method = RequestMethod.PUT)
	public String saveByAccountId(@PathVariable String accountId) {
		return "save by accound id " + accountId;
	}

	@RequestMapping(value = "/current", method = RequestMethod.GET)
	public String getCurrentAccountStatistics(Principal principal) {
		return "get current account statistics";
	}

	@RequestMapping(value = "/demo", method = RequestMethod.GET)
	public String getDemoAccountStatistics() {
		return "get current demo statistics";
	}
}
