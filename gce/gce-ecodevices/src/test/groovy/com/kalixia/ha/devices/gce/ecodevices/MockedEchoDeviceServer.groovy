package com.kalixia.ha.devices.gce.ecodevices

import groovy.util.logging.Slf4j
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.http.HttpServerRequest
import org.vertx.groovy.core.http.HttpServer

@Slf4j("LOGGER")
class MockedEchoDeviceServer {
    def HttpServer server

    def start() {
        Vertx vertx = Vertx.newVertx("localhost")
        server = vertx.createHttpServer()
        LOGGER.info("Starting VertX server...")

        server.requestHandler { HttpServerRequest req ->
            String requestURI = req.absoluteURI.toString()
            def resourceURI = requestURI.substring(requestURI.lastIndexOf('/') + 1, requestURI.length())
            LOGGER.info "Should load resource $resourceURI"
            def resource = getClass().getResource(resourceURI)
            if (resource == null) {
                LOGGER.error "Oops. Resource is null"
                req.response.with {
                    statusCode = 404
                    end()
                }
            } else {
                req.response.with {
                    putHeader "Content-Type", "application/xml"
                    end(resource.text)
                }
                LOGGER.info "Sent proper data for $resourceURI"
            }
        }

        server.listen(12345, 'localhost') { asyncResult -> println "Listen succeeded ? ${asyncResult.succeeded}" }
    }

    def stop() {
        LOGGER.info("Stopping VertX server...")
        server.close()
    }

    public static void main(String[] args) {
        new MockedEchoDeviceServer().run()
    }
}