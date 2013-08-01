package com.kalixia.ha.api

import com.kalixia.ha.dao.UsersDao
import com.kalixia.ha.model.User
import spock.lang.Specification

class UsersServiceTest extends Specification {

    def setupSpec() {
        System.setProperty("HA_HOME", new File("src/main").getAbsolutePath())
    }

    def "test service configuration"() {
        given:
        def dao = Mock(UsersDao)
        def service = new UsersServiceImpl(dao)

        when:
        service.init()

        then:
        service.configuration != null
        service.configuration.keyspace != null
        service.configuration.keyspace.strategyClass == 'SimpleStrategy'
        service.configuration.keyspace.strategyOptions != null
        service.configuration.keyspace.strategyOptions.size() == 1
        service.configuration.keyspace.strategyOptions.replication_factor == '3'
    }

    def "test creating a user"() {
        given:
        def dao = Mock(UsersDao)
        def service = new UsersServiceImpl(dao)
        def user = new User('johndoe', 'john@doe.com', 'John', 'Doe')
        dao.findByUsername(user.username) >> user

        when: "creating a user"
        service.init()
        service.saveUser(user)

        then: "expect to find it by username"
        service.findByUsername('johndoe')
    }
}
