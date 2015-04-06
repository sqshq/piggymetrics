package com.piggymetrics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.piggymetrics.dao.interfaces.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class User {

    @Autowired
    @JsonIgnore
    private UserDao userDao;

    private boolean authorized;
    private boolean capitalization;
    private boolean deposit;

    @JsonIgnore
    private String emailHash;
    @JsonIgnore
    private String lastMail;
    @JsonIgnore
    private User itself;

    private String username;
    @JsonIgnore
    private String password;
    private String checkedCurrency;
    private String lastCurrency;
    private String userpic;
    private String email;
    private String lastVisit;
    private String data;
    private String note;

    private Integer interest;
    private Integer money;
    private Integer mailing;

    private Double sliderValue;
    private Double usd;
    private Double eur;

    public String getLastVisit() {

        // @todo возвращать правду
        return "01/05/2015";
    }

}
