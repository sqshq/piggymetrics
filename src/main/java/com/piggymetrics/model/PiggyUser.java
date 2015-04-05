package com.piggymetrics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.piggymetrics.dao.interfaces.UserDao;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Getter
@Setter
public class PiggyUser {

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
    private PiggyUser itself;

    private String username;
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

    // @todo обновить интерфейс

    public void fillByName(String username) {
        userDao.select(username);
    }

    public void updateLastVisit(HttpServletRequest request) {
//        userDao.updateLastVisit(request.getRemoteAddr());
    }

    public void applyChanges(PiggyUser valid) {
//        userDao.update(valid);
    }

    public String getLastVisit() {

        // @todo возвращать правду
        return "01/05/2015";
    }

}
