package com.piggymetrics.controllers;

import com.piggymetrics.model.PiggyUser;
import com.piggymetrics.service.PiggyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class AppController {

    @Autowired
    private PiggyUserService userService;

    private ObjectMapper mapper = new ObjectMapper();

	@RequestMapping("/")
	public String launchApp(ModelMap model, Principal principal) {

        try {

            PiggyUser current = userService.getUser(principal.getName());

            model.addAttribute("user", mapper.writeValueAsString(current));
            model.addAttribute("authorized", current.isAuthorized());
        } catch (NullPointerException e) {
            model.addAttribute("authorized", false);
        } catch (Exception e) {
            // @todo log an error
        }

        return "app/base";
    }

	@RequestMapping("/demo")
	public String launchDemoApp(ModelMap model) {

        PiggyUser demo = userService.getDemoUser();

        try {
            model.addAttribute("user", mapper.writeValueAsString(demo));
            model.addAttribute("authorized", true);
            model.addAttribute("demo", true);
        } catch (Exception e) {
            // @todo log an error
        }
        return "app/base";
    }

}