package com.piggymetrics.classes.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.piggymetrics.classes.PiggyUser;
import org.springframework.jdbc.core.RowMapper;

public class UserMapper implements RowMapper
{
    private PiggyUser user;

    public UserMapper(PiggyUser user) {
        this.user = user;
    }

    public PiggyUser mapRow(ResultSet set, int rowNum) throws SQLException {

//        user.username       = set.getString("username");
//        user.data           = set.getString("data");
//        user.note           = set.getString("note");
//        user.currency       = set.getString("currency");
//        user.money          = set.getInt("money");
//        user.deposit        = set.getBoolean("deposit");
//        user.interest       = set.getInt("interest");
//        user.capitalization = set.getBoolean("capitalization");
//        user.sliderValue    = set.getDouble("slider_value");
//        user.userpic        = set.getString("userpic");
//        user.email          = set.getString("email");
//        user.emailHash      = set.getString("email_hash");
//        user.mailing        = set.getInt("mailing");
//        user.lastMail       = set.getString("last_mail");
//        user.lastVisit      = set.getString("last_visit");

        user.setUsername(set.getString("username"));
        user.setData(set.getString("data"));
        user.setNote(set.getString("note"));
        user.setCurrency(set.getString("currency"));
        user.setMoney(set.getInt("money"));
        user.setDeposit(set.getBoolean("deposit"));
        user.setInterest(set.getInt("interest"));
        user.setCapitalization(set.getBoolean("capitalization"));
        user.setSliderValue(set.getDouble("slider_value"));
        user.setUserpic(set.getString("userpic"));
        user.setEmail(set.getString("email"));
        user.setEmailHash(set.getString("email_hash"));
        user.setMailing(set.getInt("mailing"));
        user.setLastMail  (set.getString("last_mail"));
        user.setLastVisit  (set.getString("last_visit"));

        return user;
    }

}