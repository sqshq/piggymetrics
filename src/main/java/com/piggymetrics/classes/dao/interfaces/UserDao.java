package com.piggymetrics.classes.dao.interfaces;

import com.piggymetrics.classes.PiggyUser;

public interface UserDao {

    public void select(String username);

    public void insert();

    public void update(PiggyUser valid);

    public void updateLastVisit();

}
