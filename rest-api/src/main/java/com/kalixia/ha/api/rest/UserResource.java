package com.kalixia.ha.api.rest;

import com.kalixia.grapi.codecs.jaxrs.UriTemplateUtils;
import com.kalixia.grapi.codecs.rest.RESTCodec;
import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.Device;
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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static javax.ws.rs.core.HttpHeaders.CACHE_CONTROL;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "users", description = "API for Users", position = 1)
public class UserResource {
    @Inject
    UsersService service;

    @GET
    @Path("{username}")
    @ApiOperation(value = "Retrieve user by login",
            response = User.class,
            authorizations = @Authorization(value = "api_key", type = "api_key"))
    @ApiResponses({
            @ApiResponse(code = 404, message = "user not found")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = RESTCodec.HEADER_REQUEST_ID, value = "ID of the request", required = false,
                    dataType = "uuid", paramType = "header")
    })
    public Response findByUsername(@PathParam("username") @ApiParam(value = "login of the user", required = true) String username) {
        Optional<User> user = service.findByUsername(username);
        if (!user.isPresent())
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        return Response
                .ok(user)
                .link(UriTemplateUtils.createURI("/{username}/devices", username), "devices")
                .lastModified(user.get().getLastUpdateDate().toDate())
                .tag(Long.toString(user.get().getLastUpdateDate().getMillis()))
                .header(CACHE_CONTROL, "max-age=60, must-revalidate")
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create a user",
            response = User.class,
            authorizations = @Authorization(value = "api_key", type = "api_key"))
    @ApiResponses({
            @ApiResponse(code = 201, message = "created user"),
            @ApiResponse(code = 409, message = "if the user already exists")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = RESTCodec.HEADER_REQUEST_ID, value = "ID of the request", required = false,
                    dataType = "uuid", paramType = "header")
    })
    public Response createUser(@Valid User user) throws URISyntaxException {
        if (service.findByUsername(user.getUsername()) == null) {
            service.createUser(user);
            URI userURI = new URI(UriTemplateUtils.createURI("/{username}", user.getUsername()));
            return Response
                    .created(userURI).entity(user).build();
        } else {    // user already exists; should use PUT method on the resource instead!
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(Errors.withErrors(
                            new ErrorMessage("The user '%s' already exists. Use PUT method on the resource instead!",
                                    user.getUsername())))
                    .build();
        }
    }

    @PUT
    @Path("{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update a user",
            authorizations = @Authorization(value = "api_key", type = "api_key"))
    @ApiResponses({
            @ApiResponse(code = 200, message = "if the user was updated"),
            @ApiResponse(code = 404, message = "if the user can't be found")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = RESTCodec.HEADER_REQUEST_ID, value = "ID of the request", required = false,
                    dataType = "uuid", paramType = "header")
    })
    public Response updateUser(@PathParam("username") @ApiParam(value = "login of the user", required = true) String username,
                               @Valid User user) throws URISyntaxException {
        if (service.findByUsername(username) == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(Errors.withErrors(
                            new ErrorMessage("The user '%s' does not exist. Create it first via a POST request.",
                                    username)))
                    .build();
        } else {
            service.saveUser(user);
            return Response.ok().build();
        }
    }

}
