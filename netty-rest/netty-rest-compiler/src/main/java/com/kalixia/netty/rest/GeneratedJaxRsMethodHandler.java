package com.kalixia.netty.rest;

import com.kalixia.netty.rest.ApiRequest;
import com.kalixia.netty.rest.ApiResponse;

public interface GeneratedJaxRsMethodHandler {

    /**
     * Indicates if a {@link ApiRequest} should be handled by this handler.
     * @param request the request to be matched against
     * @return returns true if the {@link ApiRequest} should be handled by this handler.
     */
    boolean matches(ApiRequest request);

    /**
     * Handles an {@link ApiRequest}
     * @param request the request to handle
     * @return the {@link} ApiResponse
     */
    ApiResponse handle(ApiRequest request) throws Exception;

}
