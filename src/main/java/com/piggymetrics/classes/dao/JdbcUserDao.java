package com.piggymetrics.classes.dao;

import com.piggymetrics.classes.PiggyUser;
import com.piggymetrics.classes.dao.interfaces.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Date;

@Repository
public class JdbcUserDao extends JdbcDaoSupport implements UserDao {

    @Autowired
    private PiggyUser user;

    @Autowired
    JdbcUserDao(DataSource dataSource){
        setJdbcTemplate(new JdbcTemplate(dataSource));
        setDataSource(dataSource);
    }

    @Override
    public void select(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        getJdbcTemplate().queryForObject(
                sql, new Object[] { username }, new UserMapper(user));

    }

    @Override
    public void insert() {

    }

    @Override
    public void update(PiggyUser valid) {
        getJdbcTemplate().update(
            "UPDATE users SET checked_currency = ?, last_currency = ?, capitalization = ?, slider_value = ?, interest = ?, deposit = ?, money = ?, note = ?, data = ? where username = ?",
            valid.getCheckedCurrency(),
            valid.getLastCurrency(),
            valid.isCapitalization(),
            valid.getSliderValue(),
            valid.getInterest(),
            valid.isDeposit(),
            valid.getMoney(),
            valid.getNote(),
            valid.getData(),
            user.getUsername()
        );
    }

    @Override
    public void updateLastVisit() {
        String sql = "UPDATE users SET last_visit = ? where username = ?";

        getJdbcTemplate().update(
                sql, new Object[]{new Date(), user.getUsername()});

    }
}