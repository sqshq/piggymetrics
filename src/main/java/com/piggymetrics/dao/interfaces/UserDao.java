package com.piggymetrics.dao.interfaces;

import com.piggymetrics.model.PiggyUser;

public interface UserDao {

    public PiggyUser select(String username);

    public void insert();

//    public void update(User valid);

//    public void updateLastVisit(String IP);

}
