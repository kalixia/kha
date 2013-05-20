package com.kalixia.ha.api.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/nothing")
public class DoNothingResource {
    @POST
    public void doNothing() {
    }
}
