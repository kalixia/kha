package com.kalixia.ha.api.security;

import com.kalixia.ha.dao.UsersDao;
import com.kalixia.ha.model.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

class KhaRealm extends AuthorizingRealm {
    private final UsersDao usersDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(KhaRealm.class);

    public KhaRealm(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;

        SimpleAccount account = getAccountFromUsername(upToken.getUsername());

        if (account != null) {
            if (account.isLocked()) {
                throw new LockedAccountException("Account [" + account + "] is locked.");
            }
            if (account.isCredentialsExpired()) {
                String msg = "The credentials for account [" + account + "] are expired";
                throw new ExpiredCredentialsException(msg);
            }
        }

        return account;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = getAvailablePrincipal(principals).toString();
        return getAccountFromUsername(username);
    }

    private SimpleAccount getAccountFromUsername(String username) {
        Optional<User> optionalUser = usersDao.findByUsername(username);
        if (!optionalUser.isPresent())
            return null;

        User user = optionalUser.get();
        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(toSet());
        Set<Permission> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(WildcardPermission::new)
                .collect(toSet());
        return new SimpleAccount(user.getUsername(), user.getPassword(), user.getName(), roles, permissions);
    }

}
