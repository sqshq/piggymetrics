package com.piggymetrics.controllers;

import com.piggymetrics.model.User;
import com.piggymetrics.helpers.ResponseBody;
import com.piggymetrics.service.UserService;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;


@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Secured("ROLE_USER")
    @RequestMapping("/save/{data}")
    public ResponseBody saveChanges(@PathVariable String data, @Valid User user, BindingResult result, Principal principal) {

        if (result.hasErrors() || principal == null) {
            // @todo log an error
            return new ResponseBody("fail", result.toString());
        }

        try {
            if (data.equals("changes")) {
                userService.saveChanges(principal.getName(), user);
            } else if (data.equals("email")) {
                userService.saveEmail(principal.getName(), user);
            }
        } catch (Exception e) {
            // @todo log an error
            return new ResponseBody("fail", e.getMessage()); // @todo отдавать ланг вместо эксепшена
        }

        return new ResponseBody("success");
    }

    @RequestMapping("/register")
    public Object registerUser(@Valid User register, BindingResult result, HttpServletRequest request) {

        if (result.hasErrors()) {
            return new ResponseBody("fail", result.toString());
        }

        try {
            userService.addUser(register, request);
            return userService.getUser(register.getUsername(), request);
        } catch (Exception e) {
            return new ResponseBody("fail", e.getMessage()); // @todo отдавать ланг вместо эксепшена
        }
    }
}
