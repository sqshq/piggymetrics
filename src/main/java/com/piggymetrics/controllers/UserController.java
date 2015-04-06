package com.piggymetrics.controllers;

import com.piggymetrics.helpers.LangMessage;
import com.piggymetrics.model.User;
import com.piggymetrics.helpers.ResponseBody;
import com.piggymetrics.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
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

    @Autowired
    private LangMessage lang;

    @Secured("ROLE_USER")
    @RequestMapping("/save/{data}")
    public ResponseBody saveChanges(@PathVariable String data, @Valid User user, BindingResult result, HttpServletRequest request, Principal principal) {

        if (result.hasErrors()) {
            // @todo log an error
            return new ResponseBody("fail", lang.get(result.getFieldError().getDefaultMessage(), request));
        }

        try {
            if (data.equals("email")) {
                userService.saveEmail(principal.getName(), user);
            } else {
                userService.saveChanges(principal.getName(), user);
            }
        } catch (DuplicateKeyException e) {
            return new ResponseBody("fail", lang.get("emailExists", request));
        } catch (Exception e) {
            // @todo log an error
            return new ResponseBody("fail", lang.get("error", request));
        }

        return new ResponseBody("success");
    }

    @RequestMapping("/register")
    public Object registerUser(@Valid User register, BindingResult result, HttpServletRequest request) {

        if (result.hasErrors()) {
            return new ResponseBody("fail", lang.get(result.getFieldError().getDefaultMessage(), request));
        }

        try {
            userService.addUser(register, request);
            return userService.getUser(register.getUsername(), request);
        } catch (DuplicateKeyException e) {
            return new ResponseBody("fail", lang.get("usernameExists", request));
        } catch (Exception e) {
            // @todo log an error
            return new ResponseBody("fail", lang.get("error", request));
        }
    }
}
