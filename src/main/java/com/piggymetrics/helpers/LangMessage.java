package com.piggymetrics.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class LangMessage {

    @Autowired
    private MessageSource messageSource;

    public String get(String code, HttpServletRequest request) {
        return messageSource.getMessage(code, null, request.getLocale());
    }

}