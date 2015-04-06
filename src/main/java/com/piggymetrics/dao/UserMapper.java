package com.piggymetrics.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import com.piggymetrics.model.User;

public class UserMapper implements RowMapper {

    public User mapRow(ResultSet set, int rowNum) throws SQLException {

        User user = new User();

        user.setDeposit(set.getBoolean("deposit"));
        user.setCapitalization(set.getBoolean("capitalization"));

        user.setUsername(set.getString("username"));
        user.setCheckedCurrency(set.getString("checked_currency"));
        user.setLastCurrency(set.getString("last_currency"));
        user.setUserpic(set.getString("userpic"));
        user.setEmail(set.getString("email"));
        user.setEmailHash(set.getString("email_hash"));
        user.setLastVisit(set.getString("last_visit"));
        user.setLastMail(set.getString("last_mail"));
        user.setData(set.getString("data"));
        user.setNote(set.getString("note"));

        user.setInterest(set.getInt("interest"));
        user.setMoney(set.getInt("money"));
        user.setMailing(set.getInt("mailing"));
        user.setSliderValue(set.getDouble("slider_value"));
        user.setUsd(set.getDouble("usd"));
        user.setEur(set.getDouble("eur"));

        user.setAuthorized(true);

        return user;
    }

}