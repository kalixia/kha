package com.kalixia.ha.model.configuration

import spock.lang.Specification

class ConfigurationBuilderTest extends Specification {

    def "parse basic YAML configuration from memory"() {
        when:
        BasicConfiguration configuration = ConfigurationBuilder.fromStream(new StringReader("firstname: john\nlastname: doe"), BasicConfiguration.class);

        then:
        configuration != null
        configuration.firstname == 'john'
        configuration.lastname == 'doe'
    }

    def "parse nested YAML configuration from resource configuration file"() {
        given:
        String configurationFile = getClass().package.name.replace('.', '/') + "/nested.yml"
        def stream = getClass().classLoader.getResourceAsStream(configurationFile)
        assert stream != null

        when:
        NestedConfiguration configuration = ConfigurationBuilder.fromStream(new InputStreamReader(stream), NestedConfiguration.class);

        then:
        configuration != null
        configuration.aParameter == 'one'
        configuration.andAnotherOne == 'two'
        configuration.basic.firstname == 'john'
        configuration.basic.lastname == 'doe'
        configuration.andTheLastOne == 'three'

    }

}
