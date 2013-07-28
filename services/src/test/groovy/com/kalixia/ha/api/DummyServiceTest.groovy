package com.kalixia.ha.api

import spock.lang.Specification

class DummyServiceTest extends Specification {

    def "check configuration of service"() {
        given: "a dummy service"
        def service = new DummyService()

        when: "the service is initialized"
        service.init()

        then: "it's configuration is properly loaded"
        def conf = service.configuration
        assert conf != null
        assert conf.something == 'else'
        assert conf.foo == 'bar'
    }

}
