package com.piggymetrics.notification.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

	@RequestMapping(path = "/value", method = RequestMethod.GET)
	public String getValue() {
		return "hello!";
	}
}
