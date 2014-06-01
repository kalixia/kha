package com.kalixia.ha.api.security;

import com.kalixia.grapi.codecs.shiro.OAuthAuthorizationServer;
import com.kalixia.ha.dao.UsersDao;
import com.kalixia.ha.model.User;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        User user = null;
        try {
            user = usersDao.findByOAuthAccessToken(accessToken);
        } catch (Exception e) {
            LOGGER.error(String.format("Can't find user from OAuth2 access token '%s'", accessToken), e);
        }
        if (user == null)
            return null;

        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(toSet());
        Set<Permission> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(WildcardPermission::new)
                .collect(toSet());
        return new SimpleAccount(user.getUsername(), accessToken, user.getName(), roles, permissions);
    }
}
