package com.piggymetrics.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.piggymetrics.classes.PiggyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

@Controller
public class AppController implements MessageSourceAware {

    @Autowired
    private PiggyUser user;

    private MessageSource messageSource;
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

	@RequestMapping("/")
	public String launchApp(ModelMap model) {

        try {
            model.addAttribute("user", mapper.writeValueAsString(user));
            model.addAttribute("authorized", user.isAuthorized());
        } catch (JsonProcessingException e) {
            // @todo log an error
        }

		return "app/base";
	}

	@RequestMapping("/demo")
	public String launchDemoApp(ModelMap model) {

        Locale locale = LocaleContextHolder.getLocale();
        user.fillByName(messageSource.getMessage("demo", null, locale));
        user.setUsername("demo");

        try {
            model.addAttribute("user", mapper.writeValueAsString(user));
            model.addAttribute("authorized", true);
            model.addAttribute("demo", true);
        } catch (JsonProcessingException e) {
            // @todo log an error
        }
        return "app/base";
    }

}