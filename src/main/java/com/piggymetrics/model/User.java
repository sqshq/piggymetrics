package com.piggymetrics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.piggymetrics.dao.interfaces.UserDao;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;
import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

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

    @Length(min = 3, max = 20, message = "badUsername")
    private String username;

    @JsonIgnore
    @Length(min = 6, max = 80, message = "badPassword")
    private String password;

    @Email(message = "badEmail")
    private String email;
    private String checkedCurrency;
    private String lastCurrency;
    private String userpic;
    private String lastVisit;
    private String data;
    private String note;

    @Min(0)
    private Integer money;
    private Integer interest;
    private Integer mailing;

    private Double sliderValue;
    private Double usd;
    private Double eur;

    public String getLastVisit() {

        DateTimeFormatter input  = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S");
        DateTimeFormatter output = DateTimeFormat.forPattern("dd/MM/yyyy");

        DateTime visit = input.parseDateTime(this.lastVisit);

        return output.print(visit);
    }

}
