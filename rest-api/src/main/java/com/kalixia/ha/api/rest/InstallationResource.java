package com.kalixia.ha.api.rest;

import com.kalixia.grapi.codecs.rest.RESTCodec;
import com.kalixia.ha.api.InstallationService;
import com.kalixia.ha.model.User;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.Authorization;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;

@Path("/install")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "install", description = "API for Hub Installation", position = 5)
public class InstallationResource {
    @Inject
    InstallationService service;

    @GET
    @Path("/done")
    @ApiOperation(value = "Indicates if the installation is already done",
            response = Boolean.class,
            authorizations = @Authorization(value = "api_key", type = "api_key"))
    @ApiResponses({
            @ApiResponse(code = 200, message = "whether or not installation is already done", response = Boolean.class),
            @ApiResponse(code = 400, message = "if the request ID is not a valid UUID")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = RESTCodec.HEADER_REQUEST_ID, value = "ID of the request", required = false,
                    dataType = "uuid", paramType = "header")
    })
    public Response isDone() {
        return Response
                .status(OK)
                .entity(service.isSetupDone())
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Setup the Hub with the given user as the administrator",
            response = User.class,
            authorizations = @Authorization(value = "api_key", type = "api_key"))
    @ApiResponses({
            @ApiResponse(code = 201, message = "when the user if properly created", response = User.class),
            @ApiResponse(code = 400, message = "if the user to create and use as the administrator is invalid"),
            @ApiResponse(code = 403, message = "if the installation has already been done")
    })
    public Response installForUser(@Valid @ApiParam("the user to whom the hub will belong to") User user) {
        // if the admin user has already been create, reject request
        if (service.isSetupDone()) {
            return Response
                    .status(FORBIDDEN)
                    .build();
        }
        User created = service.installFor(user);
        return Response
                .status(CREATED)
                .entity(created)
                .build();
    }

}
