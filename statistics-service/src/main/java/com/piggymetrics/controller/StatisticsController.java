package com.piggymetrics.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class StatisticsController {

	//@PreAuthorize("#oauth2.hasScope('ui')")
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String hello(Principal principal) {
		return "hello from statistics service, " + principal.getName();
	}
}
