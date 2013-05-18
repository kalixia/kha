package com.kalixia.ha.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.netty.rest.ApiRequest;
import com.kalixia.netty.rest.ApiResponse;
import com.kalixia.netty.rest.codecs.jaxrs.GeneratedJaxRsMethodHandler;
import com.kalixia.netty.rest.codecs.jaxrs.UriTemplateUtils;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;

import javax.ws.rs.core.MediaType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloResourceMethodHandler implements GeneratedJaxRsMethodHandler {
    private final HelloResource delegate = new HelloResource();
    private final ObjectMapper objectMapper;
    private static final String URI_TEMPLATE = "/hello";

    public HelloResourceMethodHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean matches(ApiRequest request) {
        return UriTemplateUtils.extractParameters(URI_TEMPLATE, request.uri()).size() > 0;
    }

    @Override
    public ApiResponse handle(ApiRequest request) throws Exception {
        Object result = delegate.hello();
        byte[] content = objectMapper.writeValueAsBytes(result);
        return new ApiResponse(request.id(), HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(content), MediaType.APPLICATION_JSON);
    }
}
