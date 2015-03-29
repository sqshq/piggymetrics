package com.piggymetrics.controllers;

import com.piggymetrics.classes.PiggyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;

@Controller
public class AppController {

	@Autowired
	private PiggyUser user;

	@RequestMapping("/")
	public String launchApp(ModelMap model) {

		model.addAttribute("custom", user.getNote());

		model.addAttribute("authorized", false);
		model.addAttribute("user_data", false);
		return "app/base";
	}

	@RequestMapping("/demo")
	public String launchDemoApp(ModelMap model, @AuthenticationPrincipal PiggyUser piggyUser) {

		model.addAttribute("custom" , piggyUser.getUsername());

		model.addAttribute("authorized" , false);
		model.addAttribute("user_data"  , false);
		return "app/base";
	}

}