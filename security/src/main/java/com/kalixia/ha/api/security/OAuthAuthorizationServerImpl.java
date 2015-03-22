package com.kalixia.ha.api.security;

import com.kalixia.grapi.codecs.shiro.OAuth2Realm;
import com.kalixia.grapi.codecs.shiro.OAuthAuthorizationServer;
import com.kalixia.ha.dao.UsersDao;
import com.kalixia.ha.model.User;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class OAuthAuthorizationServerImpl implements OAuthAuthorizationServer {
    private final UsersDao usersDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthAuthorizationServerImpl.class);

    public OAuthAuthorizationServerImpl(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    @Override
    public SimpleAccount getAccountFromAccessToken(String accessToken) {
        Optional<User> optionalUser;
        try {
            optionalUser = usersDao.findByOAuthAccessToken(accessToken);
        } catch (Exception e) {
            LOGGER.error(String.format("Can't find user from OAuth2 access token '%s'", accessToken), e);
            return null;
        }
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
        return new SimpleAccount(user.getUsername(), accessToken, OAuth2Realm.class.getSimpleName(), roles, permissions);
    }
}
