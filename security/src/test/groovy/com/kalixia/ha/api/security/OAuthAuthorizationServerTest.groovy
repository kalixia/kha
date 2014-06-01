package com.kalixia.ha.api.security

import com.kalixia.grapi.codecs.shiro.OAuthAuthorizationServer
import com.kalixia.ha.dao.UsersDao
import com.kalixia.ha.model.security.OAuthTokens
import com.kalixia.ha.model.security.Role
import com.kalixia.ha.model.User
import org.apache.shiro.authc.SimpleAccount
import spock.lang.Specification

class OAuthAuthorizationServerTest extends Specification {

    def "test retreival of user from its oauth token"() {
        given:
        def tokens = new OAuthTokens("123456", "654321")
        UsersDao usersDao = Mock(UsersDao)
        User user = new User("johndoe", "password", "john@doe.com", "John", "Doe",
                [Role.USER] as Set<Role>, [tokens] as Set<OAuthTokens>
        )
        usersDao.findByOAuthAccessToken(tokens.accessToken) >> user
        OAuthAuthorizationServer authServer = new OAuthAuthorizationServerImpl(usersDao)

        when:
        SimpleAccount account = authServer.getAccountFromAccessToken(tokens.accessToken)

        then:
        account != null
        account.credentials == tokens.accessToken
    }

}