package com.kalixia.ha.api.security

import com.kalixia.ha.dao.UsersDao
import com.kalixia.ha.model.security.Role
import com.kalixia.ha.model.User
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.UnknownAccountException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.mgt.DefaultSecurityManager
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.Factory
import org.joda.time.DateTime
import spock.lang.Specification

import static java.util.Collections.emptySet

class KhaRealmTest extends Specification {

    def "test Shiro realm"() {
        given: "a user service having only John Doe as an administrator"
        def dao = Mock(UsersDao)
        dao.findByUsername('john') >> Optional.of(new User("john", "doe", "john@doe.com", "John", "Doe",
                [Role.ADMINISTRATOR] as Set<Role>, emptySet(), DateTime.now(), DateTime.now()))
        dao.findByUsername(_) >> Optional.empty()

        and:
        Factory<SecurityManager> factory = new org.apache.shiro.util.AbstractFactory<SecurityManager>() {
            @Override
            protected SecurityManager createInstance() {
                def manager = new DefaultSecurityManager()
                def realm = new KhaRealm(dao)
                manager.setRealm(realm)
                return manager
            }
        }
        SecurityManager securityManager = factory.getInstance()
        SecurityUtils.setSecurityManager(securityManager)

        when: "retrieving current user"
        Subject currentUser = SecurityUtils.getSubject()
        println currentUser

        then: "expect it to be non null"
        currentUser != null

        when: "logging in as john"
        UsernamePasswordToken token = new UsernamePasswordToken("john", "doe")
        token.rememberMe = true
        currentUser.login(token)
        println currentUser.principal

        then: "current user principal should be non null"
        currentUser.principal != null
        currentUser.hasRole(Role.ADMINISTRATOR.name())
        !currentUser.hasRole(Role.ANONYMOUS.name())
        currentUser.isPermitted("users:create")
        !currentUser.isPermitted("nothing:create")

        when: "logging in as an unknow user"
        token = new UsernamePasswordToken("foo", "bar")
        token.rememberMe = true
        currentUser.login(token)

        then:
        thrown UnknownAccountException
    }

}
