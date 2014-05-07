package com.kalixia.ha.devices.gce.ecodevices

import groovy.util.logging.Slf4j
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.netty.RxNetty
import io.reactivex.netty.protocol.http.server.HttpServer
import io.reactivex.netty.protocol.http.server.HttpServerRequest
import io.reactivex.netty.protocol.http.server.HttpServerResponse
import io.reactivex.netty.protocol.http.server.RequestHandler

@Slf4j("LOGGER")
class MockedEchoDeviceServer {
    def HttpServer<ByteBuf, ByteBuf> server;

    def start() {
        LOGGER.info("Starting fake EcoDevice HTTP server...")
        server = RxNetty.createHttpServer(12345, new RequestHandler<ByteBuf, ByteBuf>() {
            @Override
            rx.Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
                String requestURI = request.uri
                def resourceURI = requestURI.substring(requestURI.lastIndexOf('/') + 1, requestURI.length())
                LOGGER.info "Should load resource $resourceURI"
                def resource = getClass().getResource(resourceURI)
                if (resource == null) {
                    LOGGER.error "Oops. Resource is null"
                    response.status = HttpResponseStatus.NOT_FOUND
                    return response.writeStringAndFlush("Not Found\n")
                } else {
                    response.status = HttpResponseStatus.OK
                    response.headers.add("Content-Type", "application/xml")
                    response.writeStringAndFlush(resource.text)
                    LOGGER.info "Sent proper data for $resourceURI"
                }
            }
        });

        server.start();
    }

    def stop() {
        LOGGER.info("Stopping fake EcoDevice HTTP server...")
        server.shutdown()
    }

}