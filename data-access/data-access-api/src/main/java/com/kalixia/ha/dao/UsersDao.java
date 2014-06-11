package com.kalixia.ha.dao;

import com.kalixia.ha.model.User;
import groovy.lang.Closure;
import rx.Observable;

public interface UsersDao {
    User findByUsername(String username);
    User findByOAuthAccessToken(String token);
    Observable<User> findUsers();
    long getUsersCount();
    void save(User user);
}
