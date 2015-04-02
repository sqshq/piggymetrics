package com.piggymetrics.controllers;

import com.piggymetrics.classes.PiggyUser;
import com.piggymetrics.helpers.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/user")
public class UserController {

    /*
    * Набор рест-методов
    *
    * Reqistration
    *
    * Login
    *
    * Logout
    *
    * Save
    *
    * Upload
    *
     */

    @Autowired
    private PiggyUser user;

    @RequestMapping("/save")
    public ResponseBody launchApp(@Valid PiggyUser valid, BindingResult result) {

        if (result.hasErrors()) {
            return new ResponseBody("fail", result.toString());
        }

        try {
            user.applyChanges(valid);
        } catch (Exception e) {
            return new ResponseBody("fail", e.getMessage());
        }

        return new ResponseBody("success");
    }
}
