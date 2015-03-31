package com.piggymetrics.classes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.piggymetrics.classes.dao.interfaces.UserDao;
import com.piggymetrics.classes.interfaces.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class PiggyUser implements User {

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

    public void fillByName(String username) {
        userDao.select(username);
        userDao.updateLastVisit(); //@todo сюда же IP

        setAuthorized(true);
    }
// @todo кастомный сеттер lastVisit ибо надо/вот/так
}
