package com.kalixia.ha.api.rest;

import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.model.User;
import com.kalixia.rawsag.codecs.jaxrs.UriTemplateUtils;
import com.netflix.astyanax.util.TimeUUIDUtils;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    UsersService service;

//    @GET
//    public @NotNull List<? extends Device> findAllDevicesOfUser(@PathParam("username") String username) {
//        return devicesService.findAllDevicesOfUser(username).toList().toBlockingObservable().single();
//    }

//    @GET
//    @Path("{id}")
//    public @NotNull Device findDeviceById(@PathParam("id") UUID id) {
//        return devicesService.findDeviceById(id).toBlockingObservable().single();
//    }

    /**
     * Create a {@link User}.
     * <p>
     * <tt>curl -i -H "Accept: application/json" -X POST -d "{ username: 'jeje' }" http://localhost:8082</tt>
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(Map json) throws URISyntaxException {
        String username = (String) json.get("username");
        checkArgument(username != null, "Missing username");

        service.saveUser(new User(TimeUUIDUtils.getUniqueTimeUUIDinMicros(), username));

        URI userURI = new URI(UriTemplateUtils.createURI("/{username}", username));
        return Response.created(userURI).build();
    }

}
