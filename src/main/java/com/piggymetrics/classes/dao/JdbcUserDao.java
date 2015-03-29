package com.piggymetrics.classes.dao;

import com.piggymetrics.classes.PiggyUser;
import com.piggymetrics.classes.dao.interfaces.UserDao;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

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
    public void update() {

    }
}