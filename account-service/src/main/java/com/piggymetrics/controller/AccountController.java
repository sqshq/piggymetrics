package com.piggymetrics.controller;

import com.piggymetrics.client.StatisticsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class AccountController {

	@Autowired
	private StatisticsClient client;

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public String hello(Principal principal) {
		return "hello from account service, " + principal.getName();
	}

	//@PreAuthorize("#oauth2.hasScope('ui')")
	@RequestMapping(path = "/fire", method = RequestMethod.GET)
	public String fire(Principal principal) {
		return "fired: " + client.fire();
	}
}
