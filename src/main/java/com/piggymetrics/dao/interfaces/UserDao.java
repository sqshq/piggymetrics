package com.piggymetrics.dao.interfaces;

import com.piggymetrics.model.User;

public interface UserDao {

    public User select(String username);

    public void update(String username, User user);

    public void updateVisit(String username, String IP);

    public void insertUser(User user);

}
