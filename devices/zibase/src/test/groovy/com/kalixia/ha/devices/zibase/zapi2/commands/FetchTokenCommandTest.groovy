package com.kalixia.ha.devices.zibase.zapi2.commands

class FetchTokenCommandTest extends AbstractZibaseCommandTest {

    def "fetch token from demo account"() {
        given:
        FetchTokenCommand cmd = new FetchTokenCommand(configuration, httpClient, mapper)

        when:
        def token = cmd.execute()

        then:
        token != ''
    }

}