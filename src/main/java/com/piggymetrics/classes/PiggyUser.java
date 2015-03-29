package com.piggymetrics.classes;

import com.piggymetrics.classes.dao.interfaces.UserDao;
import com.piggymetrics.classes.interfaces.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
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
    private String emailHash;
    private String lastVisit;
    private String lastMail;
    private String data;
    private String note;

    private Integer interest;
    private Integer money;
    private Integer mailing;
    private Double sliderValue;


    public void setByName(String username) {
        userDao.select(username);
        userDao.updateLastVisit();

        setAuthorized(true);
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public void setSliderValue(Double sliderValue) {
        this.sliderValue = sliderValue;
    }

    public void setCapitalization(boolean capitalization) {
        this.capitalization = capitalization;
    }

    public void setDeposit(boolean deposit) {
        this.deposit = deposit;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setUserpic(String userpic) {
        this.userpic = userpic;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }

    public void setLastVisit(String lastVisit) {
        this.lastVisit = lastVisit;
    }

    public void setLastMail(String lastMail) {
        this.lastMail = lastMail;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setInterest(Integer interest) {
        this.interest = interest;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public void setMailing(Integer mailing) {
        this.mailing = mailing;
    }



    public String getUsername() {
        return username;
    }

    public Double getSliderValue() {
        return sliderValue;
    }

    public boolean isCapitalization() {
        return capitalization;
    }

    public boolean isDeposit() {
        return deposit;
    }

    public String getCurrency() {
        return currency;
    }

    public String getUserpic() {
        return userpic;
    }

    public String getEmail() {
        return email;
    }

    public String getEmailHash() {
        return emailHash;
    }

    public String getLastVisit() {
        return lastVisit;
    }

    public String getLastMail() {
        return lastMail;
    }

    public String getData() {
        return data;
    }

    public String getNote() {
        return note;
    }

    public Integer getInterest() {
        return interest;
    }

    public Integer getMoney() {
        return money;
    }

    public Integer getMailing() {
        return mailing;
    }

    @Override
    public String toString() {
        return "PiggyUser{" +
                "userDao=" + userDao +
                ", authorized=" + authorized +
                ", capitalization=" + capitalization +
                ", deposit=" + deposit +
                ", username='" + username + '\'' +
                ", currency='" + currency + '\'' +
                ", userpic='" + userpic + '\'' +
                ", email='" + email + '\'' +
                ", emailHash='" + emailHash + '\'' +
                ", lastVisit='" + lastVisit + '\'' +
                ", lastMail='" + lastMail + '\'' +
                ", data='" + data + '\'' +
                ", note='" + note + '\'' +
                ", interest=" + interest +
                ", money=" + money +
                ", mailing=" + mailing +
                ", sliderValue=" + sliderValue +
                '}';
    }
}
