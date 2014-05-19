package com.kalixia.ha.api

import com.kalixia.ha.dao.UsersDao
import com.kalixia.ha.model.User
import spock.lang.Specification

class UsersServiceTest extends Specification {

    def setupSpec() {
        System.setProperty("app.home", new File("src/main").getAbsolutePath())
    }

    def "test service configuration"() {
        given:
        def dao = Mock(UsersDao)
        def service = new UsersServiceImpl(dao)

        when:
        service.init()

        then:
        service.configuration != null
        service.configuration.storage != null
        service.configuration.storage.strategy == 'lucene'
        service.configuration.storage.lucene != null
        service.configuration.storage.lucene.directory == '/tmp/kalixia-ha'
        service.configuration.storage.cassandra != null
        service.configuration.storage.cassandra.keyspace != null
        service.configuration.storage.cassandra.keyspace.strategyClass == 'SimpleStrategy'
        service.configuration.storage.cassandra.keyspace.strategyOptions != null
        service.configuration.storage.cassandra.keyspace.strategyOptions.size() == 1
        service.configuration.storage.cassandra.keyspace.strategyOptions.replication_factor == '3'
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
