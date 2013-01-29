package com.kalixia.ha.gateway.codecs.jaxrs

import spock.lang.Unroll

class UriTemplateUtilsTest extends spock.lang.Specification {

    @Unroll
    def "uri template #uri_template is compiled to #pattern regex"() {
        expect:
        UriTemplateUtils.extractRegexPattern(uri_template).toString() == pattern

        where:
        uri_template                    | pattern
        "/devices/{id}"                 | "^/devices/(.*)\$"
        "/devices/{id}/temperature"     | "^/devices/(.*)/temperature\$"
    }

}