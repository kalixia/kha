package com.kalixia.ha.api.rest;

import javax.ws.rs.Path;

@Path("/hello")
public class HelloResource {

    public String hello() {
        return "hello";
    }

}
