package com.kalixia.ha.api;

import com.kalixia.ha.model.User;
import rx.Observable;

public interface UsersService {
    User findByUsername(String username);
    void saveUser(User user);
    Observable<User> findUsers();
    Long getUsersCount();
}
