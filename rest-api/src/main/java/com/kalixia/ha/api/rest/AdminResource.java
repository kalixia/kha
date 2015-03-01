package com.kalixia.ha.api.rest;

import com.kalixia.grapi.codecs.rest.RESTCodec;
import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.security.Permissions;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.Authorization;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import rx.Observable;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "admin", description = "API for Administration", position = 4)
public class AdminResource {
    @Inject
    UsersService service;

    @GET
    @Path("/users")
    @RequiresPermissions(Permissions.USERS_LIST)
    @ApiOperation(value = "Retrieve all users", response = User.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 200, message = "the lit of users", response = User.class),
            @ApiResponse(code = 400, message = "if the request ID is not a valid UUID")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = RESTCodec.HEADER_REQUEST_ID, value = "ID of the request", required = false,
                    dataType = "uuid", paramType = "header")
    })
    public Response getUsers() {
        Observable<User> userObs = service.findUsers();
        List<User> users = userObs.toList().toBlocking().single();
        return Response
                .status(Response.Status.OK)
                .entity(users)
                .build();
    }

    @GET
    @Path("/users/count")
    @RequiresPermissions(Permissions.USERS_COUNT)
    @ApiOperation(value = "Retrieve users count",
            response = Long.class,
            authorizations = @Authorization(value = "api_key", type = "api_key"))
    @ApiResponses({
            @ApiResponse(code = 200, message = "the count of users", response = Long.class),
            @ApiResponse(code = 400, message = "if the request ID is not a valid UUID"),
            @ApiResponse(code = 403, message = "if the authorization credentials are missing")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = RESTCodec.HEADER_REQUEST_ID, value = "ID of the request", required = false,
                    dataType = "uuid", paramType = "header")
    })
    public Response getUsersCount() {
        return Response
                .status(Response.Status.OK)
                .entity(service.getUsersCount())
                .build();
    }

}
