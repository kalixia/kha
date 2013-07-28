package com.kalixia.ha.api

import com.kalixia.ha.dao.UsersDao
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
}
