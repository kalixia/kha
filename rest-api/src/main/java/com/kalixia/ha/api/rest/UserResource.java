package com.kalixia.ha.api.rest;

import com.kalixia.grapi.codecs.jaxrs.UriTemplateUtils;
import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.model.User;

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

import static javax.ws.rs.core.HttpHeaders.CACHE_CONTROL;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    UsersService service;

    @GET
    @Path("{username}")
    public Response findByUsername(@PathParam("username") String username) {
        User user = service.findByUsername(username);
        if (user == null)
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        return Response
                .ok(user)
                .link(UriTemplateUtils.createURI("/{username}/devices", username), "devices")
                .lastModified(user.getLastUpdateDate().toDate())
                .tag(Long.toString(user.getLastUpdateDate().getMillis()))
                .header(CACHE_CONTROL, "max-age=60, must-revalidate")
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid User user) throws URISyntaxException {
        if (service.findByUsername(user.getUsername()) == null) {
            service.createUser(user);
            URI userURI = new URI(UriTemplateUtils.createURI("/{username}", user.getUsername()));
            return Response
                    .created(userURI).build();
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
    public Response updateUser(@PathParam("username") String username, @Valid User user) throws URISyntaxException {
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
