package com.kalixia.ha.api.rest;

import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.model.User;
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
public class AdminResource {
    @Inject
    UsersService service;

    @GET
    @Path("/users")
    public Response getUsers() {
        Observable<User> userObs = service.findUsers();
        List<User> users = userObs.toList().toBlockingObservable().single();
        return Response
                .status(Response.Status.OK)
                .entity(users)
                .build();
    }

    @GET
    @Path("/users/count")
    public Response getUsersCount() {
        return Response
                .status(Response.Status.OK)
                .entity(service.getUsersCount())
                .build();
    }

}
