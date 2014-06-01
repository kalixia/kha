package com.kalixia.ha.api.security;

import com.kalixia.grapi.codecs.shiro.OAuth2Realm;
import com.kalixia.grapi.codecs.shiro.OAuthAuthorizationServer;
import com.kalixia.ha.dao.UsersDao;
import dagger.Module;
import dagger.Provides;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.AbstractFactory;
import org.apache.shiro.util.Factory;

import javax.inject.Singleton;
import java.util.Arrays;

@Module(library = true, complete = false)
public class SecurityModule {

    @Provides @Singleton SecurityManager provideSecurityManager(KhaRealm khaRealm,
                                                                OAuth2Realm oauth2Realm,
                                                                CacheManager cacheManager) {
        Factory<SecurityManager> factory = new AbstractFactory<SecurityManager>() {
            @Override
            protected SecurityManager createInstance() {
                DefaultSecurityManager manager = new DefaultSecurityManager();
                manager.setRealms(Arrays.asList(khaRealm, oauth2Realm));
                manager.setCacheManager(cacheManager);
                return manager;
            }
        };
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    @Provides @Singleton KhaRealm provideKhaRealm(UsersDao usersDao, PasswordMatcher passwordMatcher) {
        KhaRealm realm = new KhaRealm(usersDao);
        realm.setCredentialsMatcher(passwordMatcher);
        realm.setAuthenticationCachingEnabled(true);
        return realm;
    }

    @Provides @Singleton OAuth2Realm provideOAuth2Realm(OAuthAuthorizationServer authorizationServer) {
        OAuth2Realm realm = new OAuth2Realm(authorizationServer);
        realm.setAuthenticationCachingEnabled(true);
        return realm;
    }

    @Provides @Singleton OAuthAuthorizationServer provideOAuthAuthorizationServer(UsersDao usersDao) {
        return new OAuthAuthorizationServerImpl(usersDao);
    }

    @Provides @Singleton PasswordService providePasswordService() {
        return new DefaultPasswordService();
    }

    @Provides @Singleton PasswordMatcher providePasswordMatcher(PasswordService passwordService) {
        PasswordMatcher passwordMatcher = new PasswordMatcher();
        passwordMatcher.setPasswordService(passwordService);
        return passwordMatcher;
    }

    @Provides @Singleton CacheManager provideCacheManager() {
        return new MemoryConstrainedCacheManager();
    }

}
