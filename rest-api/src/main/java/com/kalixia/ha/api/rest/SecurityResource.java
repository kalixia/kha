package com.kalixia.ha.api.rest;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.mgt.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.OK;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class SecurityResource {
    @Inject
    SecurityManager securityManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityResource.class);

    @POST
    @Path("/login")
    public Response login(@FormParam("login") String login, @FormParam("password") String password) {
        LOGGER.info("Should login user '{}'", login);
        // TODO: really log in user
        return Response
                .status(OK)
                .build();

    }

    @GET
    @Path("/logout")
    @RequiresUser
    public Response logout() {
        // TODO: really logout user
        return Response
                .status(OK)
                .build();

    }

}
