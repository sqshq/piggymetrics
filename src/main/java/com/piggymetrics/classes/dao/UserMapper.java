package com.piggymetrics.classes.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.piggymetrics.classes.PiggyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

public class UserMapper implements RowMapper
{
    private PiggyUser user;

    public UserMapper(PiggyUser user) {
        this.user = user;
    }

    public PiggyUser mapRow(ResultSet set, int rowNum) throws SQLException {
        System.out.println(user.isAuthorized());
        user.setNote(set.getString("note"));
        return user;
    }

}