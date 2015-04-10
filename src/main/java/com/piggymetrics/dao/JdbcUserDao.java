package com.piggymetrics.dao;

import com.piggymetrics.domain.User;
import com.piggymetrics.dao.interfaces.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Date;

@Repository
public class JdbcUserDao extends JdbcDaoSupport implements UserDao {

    @Autowired
    JdbcUserDao(DataSource dataSource){
        setJdbcTemplate(new JdbcTemplate(dataSource));
        setDataSource(dataSource);
    }

    @Override
    public void insertUser(User user) {
        String sql = "INSERT INTO users (username, password, userpic, last_visit) VALUES (?, ?, ?, ?)";

        getJdbcTemplate().update(
                sql, user.getUsername(), user.getPassword(), user.getUserpic(), new Date());
    }


    @Override
    public void saveEmail(String username, User user) {

        String sql = "UPDATE users SET email = ? where username = ?";

        getJdbcTemplate().update(
                sql, user.getEmail(), username);
    }

    @Override
    public User select(String username) {

        String sql = "SELECT * FROM users JOIN settings WHERE username = ?";

        try {
            User user = (User) getJdbcTemplate().queryForObject(
                    sql, new Object[]{username}, new UserMapper());

            return user;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void update(String username, User user) {

        getJdbcTemplate().update(

            "UPDATE users SET checked_currency = ?, last_currency = ?, capitalization = ?, slider_value = ?, " +
                    "interest = ?, deposit = ?, money = ?, note = ?, data = ? WHERE username = ?",

            user.getCheckedCurrency(),
            user.getLastCurrency(),
            user.isCapitalization(),
            user.getSliderValue(),
            user.getInterest(),
            user.isDeposit(),
            user.getMoney(),
            user.getNote(),
            user.getData(),
            username
        );
    }

    @Override
    public void updateVisit(String username, String IP, String language) {

        String sql = "UPDATE users SET last_visit = ?, IP = ?, language = ? where username = ?";

        getJdbcTemplate().update(
                sql, new Date(), IP, language, username);
    }
}