package com.piggymetrics.classes;

import com.piggymetrics.classes.dao.interfaces.UserDao;
import com.piggymetrics.classes.interfaces.User;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PiggyUser implements User {

    @Autowired
    private UserDao userDao;

    @Getter @Setter private boolean authorized;
    @Getter @Setter private boolean capitalization;
    @Getter @Setter private boolean deposit;

    @Getter @Setter private String username;
    @Getter @Setter private String currency;
    @Getter @Setter private String userpic;
    @Getter @Setter private String email;
    @Getter @Setter private String emailHash;
    @Getter @Setter private String lastVisit;
    @Getter @Setter private String lastMail;
    @Getter @Setter private String data;
    @Getter @Setter private String note;

    @Getter @Setter private Integer interest;
    @Getter @Setter private Integer money;
    @Getter @Setter private Integer mailing;

    @Getter @Setter private Double sliderValue;

    public void setByName(String username) {
        userDao.select(username);
        userDao.updateLastVisit();

        setAuthorized(true);
    }
}
