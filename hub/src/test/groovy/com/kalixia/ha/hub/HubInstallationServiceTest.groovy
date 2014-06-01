package com.kalixia.ha.hub

import com.kalixia.ha.api.UsersService
import com.kalixia.ha.api.UsersServiceImpl
import com.kalixia.ha.dao.UsersDao
import com.kalixia.ha.model.User
import com.kalixia.ha.model.security.Role
import org.apache.shiro.authc.credential.DefaultPasswordService
import spock.lang.Specification

import static jersey.repackaged.com.google.common.collect.Sets.newHashSet

class HubInstallationServiceTest extends Specification {

    def "test hub installation"() {
        given:
        User user = new User("johndoe", "password", "john@doe.com", "John", "Doe",
                [Role.ADMINISTRATOR] as Set<Role>, newHashSet())
        UsersDao usersDao = Mock(UsersDao)
        usersDao.getUsersCount() >>> [0L, 0L, 1L]
        usersDao.findByUsername('johndoe') >>> [null, user]
        UsersService usersService = new UsersServiceImpl(usersDao, new DefaultPasswordService())

        when:
        HubInstallationService installService = new HubInstallationService(usersService)

        then:
        !installService.isSetupDone()

        when:
        def created = installService.installFor(user)

        then:
        created != null
        created.oauthTokens != null
        created.oauthTokens.size() == 1
        created.oauthTokens[0].accessToken != null
        installService.setupDone
    }

    def "test hub installation can't be done more than once"() {
        given:
        UsersDao usersDao = Mock(UsersDao)
        usersDao.getUsersCount() >> 1
        UsersService usersService = new UsersServiceImpl(usersDao, new DefaultPasswordService())
        User user = new User("johndoe", "password", "john@doe.com", "John", "Doe",
                        [Role.ADMINISTRATOR] as Set<Role>, newHashSet())
        HubInstallationService installService = new HubInstallationService(usersService)

        when:
        installService.installFor(user)

        then:
        thrown(IllegalStateException)
    }

}