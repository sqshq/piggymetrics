package com.piggymetrics.dao;

import com.piggymetrics.model.PiggyUser;
import com.piggymetrics.dao.interfaces.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class JdbcUserDao extends JdbcDaoSupport implements UserDao {

    @Autowired
    JdbcUserDao(DataSource dataSource){
        setJdbcTemplate(new JdbcTemplate(dataSource));
        setDataSource(dataSource);
    }

    @Override
    public PiggyUser select(String username) {

        String sql = "SELECT * FROM users JOIN settings WHERE username = ?";

        try {
            PiggyUser user = (PiggyUser) getJdbcTemplate().queryForObject(
                    sql, new Object[]{username}, new UserMapper());

            return user;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void insert() {

    }

//    @Override
//    public void update(User valid) {
//        getJdbcTemplate().update(
//            "UPDATE users SET checked_currency = ?, last_currency = ?, capitalization = ?, slider_value = ?, interest = ?, deposit = ?, money = ?, note = ?, data = ? where username = ?",
//            valid.getCheckedCurrency(),
//            valid.getLastCurrency(),
//            valid.isCapitalization(),
//            valid.getSliderValue(),
//            valid.getInterest(),
//            valid.isDeposit(),
//            valid.getMoney(),
//            valid.getNote(),
//            valid.getData(),
//            user.getUsername()
//        );
//    }

//    @Override
//    public void updateLastVisit(String IP) {
//        String sql = "UPDATE users SET last_visit = ?, IP = ? where username = ?";
//
//        getJdbcTemplate().update(
//                sql, new Object[]{new Date(), IP, user.getUsername()});
//
//    }
}