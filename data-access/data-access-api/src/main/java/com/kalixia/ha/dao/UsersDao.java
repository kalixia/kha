package com.kalixia.ha.dao;

import com.kalixia.ha.model.User;
import groovy.lang.Closure;
import rx.Observable;

public interface UsersDao {
    User findByUsername(String username) throws Exception;
    void save(User user) throws Exception;
    Observable<User> findUsers() throws Exception;
    Long getUsersCount() throws Exception;
}
