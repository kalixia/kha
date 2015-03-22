package com.kalixia.ha.api.rest;

import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.model.User;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.Authorization;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DelegatingSubject;
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
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "security", description = "API for Security", position = 6)
public class SecurityResource {
    @Inject
    SecurityManager securityManager;

    @Inject
    UsersService usersService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityResource.class);

    @POST
    @Path("login")
    @ApiOperation(value = "Logs in the user",
            response = User.class,
            authorizations = @Authorization(value = "api_key", type = "api_key"))
    @ApiResponses({
            @ApiResponse(code = 200, message = "if the user credentials are valid", response = User.class),
            @ApiResponse(code = 401, message = "if the user credentials are invalid")
    })
    public Response login(
            @ApiParam(value = "the login of the user", required = true) @FormParam("username") String login,
            @ApiParam(value = "the password of the user", required = true) @FormParam("password") String password) {
        LOGGER.info("Log in user '{}'", login);
        try {
            Subject subject = (new Subject.Builder(securityManager)).buildSubject();
            securityManager.login(subject, new UsernamePasswordToken(login, password));
            User user = usersService.findByUsername(login);
            return Response
                    .status(OK)
                    .entity(user)
                    .build();
        } catch (AuthenticationException e) {
            return Response
                    .status(UNAUTHORIZED)
                    .build();
        }
    }

    @GET
    @Path("logout")
    @ApiOperation(value = "Logs out the user",
            authorizations = @Authorization(value = "api_key", type = "api_key"))
    @ApiResponses({
            @ApiResponse(code = 200, message = "if the user was logged out")
    })
    @RequiresUser
    public Response logout() {
        Subject subject = SecurityUtils.getSubject();
        LOGGER.info("Log out user '{}'", subject.getPrincipal());
        subject.logout();
        return Response
                .status(OK)
                .build();

    }

}
