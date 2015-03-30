package com.piggymetrics.controllers;

import com.piggymetrics.classes.PiggyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

@Controller
public class AppController implements MessageSourceAware {

    @Autowired
    private PiggyUser user;

    private MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

	@RequestMapping("/")
	public String launchApp(ModelMap model) {

		model.addAttribute("custom", user.getNote()); //@todo убрать дебаг

		model.addAttribute("authorized", user.isAuthorized());
		model.addAttribute("user", false);
		return "app/base";
	}

	@RequestMapping("/demo")
	public String launchDemoApp(ModelMap model) {

        Locale locale = LocaleContextHolder.getLocale();
        user.setByName(messageSource.getMessage("demo", null, locale));

        model.addAttribute("custom", user); //@todo убрать дебаг

        model.addAttribute("authorized", true);
        model.addAttribute("user_data", false);
        return "app/base";
    }

}