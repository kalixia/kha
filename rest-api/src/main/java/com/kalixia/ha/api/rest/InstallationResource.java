package com.kalixia.ha.api.rest;

import com.kalixia.ha.api.InstallationService;
import com.kalixia.ha.model.User;

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
public class InstallationResource {
    @Inject
    InstallationService service;

    @GET
    @Path("/done")
    public Response isDone() {
        return Response
                .status(OK)
                .entity(service.isSetupDone())
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response installForUser(@Valid User user) {
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
