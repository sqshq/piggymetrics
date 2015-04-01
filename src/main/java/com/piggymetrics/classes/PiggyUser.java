package com.piggymetrics.classes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.piggymetrics.classes.dao.interfaces.UserDao;
import com.piggymetrics.classes.interfaces.User;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

    public String getLastVisit() {

//        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

//        DateTime date = formatter.parseDateTime(this.lastVisit);
//        DateTimeFormatter fmt = DateTimeFormat.forPattern("d/MMMM/yyyy");

//        System.out.println(date);
        return "01/05/2015";
    }
}
