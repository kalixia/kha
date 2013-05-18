package com.kalixia.netty.rest.codecs.jaxrs

import spock.lang.Unroll

class UriTemplateUtilsTest extends spock.lang.Specification {

    @Unroll
    def "uri template #uri_template is properly compiled to regex pattern"() {
        expect:
        UriTemplateUtils.extractRegexPattern(uri_template).toString() == pattern

        where:
        uri_template                    | pattern
        "/devices"                      | "^/devices/?\$"
        "/devices/"                     | "^/devices/?\$"
        "/devices/{id}"                 | "^/devices/(.*)/?\$"
        "/devices/{id}/temperature"     | "^/devices/(.*)/temperature/?\$"
    }

    @Unroll
    def "template #uri_templates matches uri #uri"() {
        given:
        def pattern = UriTemplateUtils.extractRegexPattern(uri_template)

        expect:
        pattern.matcher(uri).matches()

        where:
        uri_template      | uri
        "/echo/{message}" | "/echo/test"
    }

    @Unroll
    def "extraction of parameters from #uri with template #uri_template"() {
        given:
        def parameters = UriTemplateUtils.extractParameters(uri_template, uri)

        expect:
        parameters.size() == parametersMap.size()

        where:
        uri                                | uri_template                     || parametersMap
        "/hello"                           | "/hello"                         || []
        "/echo/test"                       | "/echo/{message}"                || [ message: "test" ]
        "/users/johndoe/devices/my_device" | "/users/{user}/devices/{device}" || [ user: 'johndoe', device: 'my_device' ]
    }

}