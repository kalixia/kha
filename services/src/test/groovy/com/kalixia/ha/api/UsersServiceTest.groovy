package com.kalixia.ha.api

import com.kalixia.ha.dao.UsersDao
import com.kalixia.ha.model.User
import com.kalixia.ha.model.security.Role
import org.apache.shiro.authc.credential.PasswordService
import spock.lang.Specification

import static com.google.common.collect.Sets.newHashSet

class UsersServiceTest extends Specification {

    def setupSpec() {
        System.setProperty("app.home", new File("src/main").getAbsolutePath())
    }

    def "test service configuration"() {
        given:
        def dao = Mock(UsersDao)
        def passwordService = Mock(PasswordService)
        def service = new UsersServiceImpl(dao, passwordService)

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
        def passwordService = Mock(PasswordService)
        def service = new UsersServiceImpl(dao, passwordService)
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe', [Role.USER] as Set<Role>, newHashSet())
        dao.findByUsername(user.username) >> Optional.of(user)

        when: "creating a user"
        service.init()
        service.createUser(user)

        then: "expect to find it by username"
        service.findByUsername('johndoe')

        when: "updating a user"
        user.email = 'johndoe@gmail.com'
        service.saveUser(user)
        def found = service.findByUsername('johndoe')

        then: "expect changes to be retrieved"
        found.isPresent()
        found.get().email == 'johndoe@gmail.com'
    }
}
