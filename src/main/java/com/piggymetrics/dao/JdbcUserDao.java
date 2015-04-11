package com.piggymetrics.dao;

import com.piggymetrics.domain.User;
import com.piggymetrics.dao.interfaces.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    public User insertUser(User user, String IP, String language) {
        String sql = "INSERT INTO users (username, password, userpic, last_visit, IP, language) VALUES (?, ?, ?, ?, ?, ?)";

        getJdbcTemplate().update(
                sql, user.getUsername(), user.getPassword(), user.getUserpic(), new Date(), IP, language);

        return user;
    }

    @Override
    @CachePut(value="userCache", key="#user.username")
    public User update(User user, String IP, String language) {

        getJdbcTemplate().update(

                "UPDATE users SET checked_currency = ?, last_currency = ?, capitalization = ?, slider_value = ?, interest = ?, " +
                        "deposit = ?, money = ?, note = ?, data = ?, last_visit = ?, IP = ?, language = ? WHERE username = ?",

                user.getCheckedCurrency(),
                user.getLastCurrency(),
                user.isCapitalization(),
                user.getSliderValue(),
                user.getInterest(),
                user.isDeposit(),
                user.getMoney(),
                user.getNote(),
                user.getData(),
                new Date(),
                IP,
                language,
                user.getUsername()
        );

        return user;
    }

    @Override
    public void saveEmail(User user) {

        String sql = "UPDATE users SET email = ? where username = ?";

        getJdbcTemplate().update(
                sql, user.getEmail(), user.getUsername());
    }

    @Override
    @Cacheable(value="userCache", key="#username")
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

}