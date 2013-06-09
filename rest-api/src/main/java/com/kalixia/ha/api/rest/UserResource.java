package com.kalixia.ha.api.rest;

import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.model.User;
import com.kalixia.rawsag.codecs.jaxrs.UriTemplateUtils;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    UsersService service;

    @GET
    @Path("{username}")
    public @NotNull User findByUsername(@PathParam("username") String username) {
        return service.findByUsername(username);
    }

    /**
     * Create a {@link User}.
     * <p>
     * <tt>curl -i -H "Accept: application/json" -X POST -d "{ username: 'johndoe', email: 'john@doe.com', firstName: 'John', lastName: 'Doe' }" http://localhost:8082</tt>
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid User user) throws URISyntaxException {
        service.saveUser(user);

        URI userURI = new URI(UriTemplateUtils.createURI("/{username}", user.getUsername()));
        return Response.created(userURI).build();
    }

}
