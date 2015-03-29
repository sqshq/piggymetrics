package com.piggymetrics.classes;

import com.piggymetrics.classes.dao.interfaces.UserDao;
import com.piggymetrics.classes.interfaces.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PiggyUser implements User {

    @Autowired
    private UserDao userDao;

    private boolean authorized;
    private boolean capitalization;
    private boolean deposit;

    private String username;
    private String currency;
    private String userpic;
    private String email;
    private String email_hash;
    private String last_visit;
    private String last_mail;
    private String data;
    private String note;

    private Integer money;
    private Integer mailing;
    private Double slider_value;


    public void setByName(String username) {
        userDao.select(username);
        // Если получится найти - проставить authorized = true
        // Потом - проставить last_visit
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
