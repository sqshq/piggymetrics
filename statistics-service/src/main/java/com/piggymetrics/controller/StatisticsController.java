package com.piggymetrics.controller;

import com.piggymetrics.domain.Account;
import com.piggymetrics.domain.Statistics;
import com.piggymetrics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class StatisticsController {

	@Autowired
	private StatisticsService statisticsService;

	@PreAuthorize("#oauth2.hasScope('server') or #accountName.equals('demo')")
	@RequestMapping(value = "/{accountName}", method = RequestMethod.GET)
	public Statistics getStatisticsByAccountName(@PathVariable String accountName) {
		return statisticsService.findByAccountName(accountName);
	}

	@RequestMapping(value = "/current", method = RequestMethod.GET)
	public Statistics getCurrentAccountStatistics(Principal principal) {
		return statisticsService.findByAccountName(principal.getName());
	}

	@PreAuthorize("#oauth2.hasScope('server')")
	@RequestMapping(value = "/{accountName}", method = RequestMethod.PUT)
	public void save(@PathVariable String accountName, Account account) {
		statisticsService.save(accountName, account);
	}
}
