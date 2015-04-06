package com.piggymetrics.controllers;

import com.piggymetrics.model.User;
import com.piggymetrics.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class AppController {

    @Autowired
    private UserService userService;

    private ObjectMapper mapper = new ObjectMapper();

	@RequestMapping("/")
	public String launchApp(ModelMap model, Principal principal, HttpServletRequest request) {

        try {
            User current = userService.getUser(principal.getName(), request);
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

        try {
            User demo = userService.getDemoUser();
            model.addAttribute("user", mapper.writeValueAsString(demo));
            model.addAttribute("authorized", true);
            model.addAttribute("demo", true);
        } catch (Exception e) {
            // @todo log an error
        }

        return "app/base";
    }

}