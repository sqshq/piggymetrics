package com.piggymetrics.dao.interfaces;

import com.piggymetrics.domain.User;

public interface UserDao {

    public User insertUser(User user, String IP, String language);

    public User update(User user, String IP, String language);

    public User select(String username);

    public void saveEmail(User user);

}
