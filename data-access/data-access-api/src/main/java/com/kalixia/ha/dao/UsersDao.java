package com.kalixia.ha.dao;

import com.kalixia.ha.model.User;
import groovy.lang.Closure;
import rx.Observable;

import java.util.Optional;

public interface UsersDao {
    Optional<User> findByUsername(String username);
    Optional<User> findByOAuthAccessToken(String token);
    Observable<User> findUsers();
    long getUsersCount();
    void save(User user);
}
