package com.kalixia.ha.api.rest;

import com.kalixia.grapi.codecs.jaxrs.UriTemplateUtils;
import com.kalixia.grapi.codecs.rest.RESTCodec;
import com.kalixia.ha.api.DevicesService;
import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.DeviceMetadata;
import com.kalixia.ha.model.security.Permissions;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.Authorization;
import com.wordnik.swagger.annotations.AuthorizationScope;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.HttpHeaders.CACHE_CONTROL;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.EXPECTATION_FAILED;

@Path("/{username}/devices")
@Produces(MediaType.APPLICATION_JSON)
@Api(
        value = "devices",
        description = "API for Devices", position = 2,
        authorizations = {
                @Authorization(value = "devices_auth", type = "oauth2", scopes = {
                        @AuthorizationScope(scope = "write:devices", description = "modify your devices"),
                        @AuthorizationScope(scope = "read:devices", description = "read your devices")
                })
        }
)
public class DeviceResource {
    @Inject
    DevicesService devicesService;

    @Inject
    UsersService usersService;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResource.class);

    @GET
    @RequiresPermissions(Permissions.DEVICES_VIEW)
    @ApiOperation(value = "Retrieve the devices of a user",
            response = Device.class, responseContainer = "List",
            authorizations = @Authorization(value = "api_key", type = "api_key"))
    @ApiResponses({
            @ApiResponse(code = 200, message = "the list of devices the user has"),
            @ApiResponse(code = 400, message = "if the request ID is not a valid UUID"),
            @ApiResponse(code = 403, message = "if the request is denied for security reasons")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = RESTCodec.HEADER_REQUEST_ID, value = "ID of the request", required = false,
                    dataType = "uuid", paramType = "header")
    })
    public @NotNull Response findAllDevicesOfUser(
            @ApiParam(value = "the owner of the devices", required = true) @PathParam("username") String username) {
        List<? extends Device> devices = devicesService.findAllDevicesOfUser(username)
                .toList().toBlocking().single();
        return Response
                .ok(devices)
                .link(UriTemplateUtils.createURI("/{username}", username), "owner")
                .header(CACHE_CONTROL, "max-age=60, must-revalidate")
                .build();
    }

    @GET
    @Path("/{name}")
    @RequiresPermissions(Permissions.DEVICES_VIEW)
    @ApiOperation(value = "Retrieve the device of a user having the given name",
            response = Device.class,
            authorizations = @Authorization(value = "api_key", type = "api_key"))
    @ApiResponses({
            @ApiResponse(code = 200, message = "the device"),
            @ApiResponse(code = 400, message = "if the request ID is not a valid UUID"),
            @ApiResponse(code = 403, message = "if the request is denied for security reasons"),
            @ApiResponse(code = 404, message = "if the device does not exist")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = RESTCodec.HEADER_REQUEST_ID, value = "ID of the request", required = false,
                    dataType = "uuid", paramType = "header")
    })
    public @NotNull Response findDeviceByName(
            @ApiParam(value = "the owner of the device", required = true) @PathParam("username") String username,
            @ApiParam(value = "the name of the device", required = true) @PathParam("name") String name) {
        Optional<Device> optionalDevice = devicesService.findDeviceByName(username, name);
        if (!optionalDevice.isPresent())
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        Device device = optionalDevice.get();
        return Response
                .ok(device)
                .link(UriTemplateUtils.createURI("/{username}", username), "owner")
                .lastModified(device.getLastUpdateDate().toDate())
                .tag(Long.toString(device.getLastUpdateDate().getMillis()))
                .header(CACHE_CONTROL, "max-age=60, must-revalidate")
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RequiresPermissions(Permissions.DEVICES_CREATE)
    @ApiOperation(value = "Create a new device for the user",
            response = Device.class,
            authorizations = @Authorization(value = "api_key", type = "api_key"))
    @ApiResponses({
            @ApiResponse(code = 201, message = "the created device"),
            @ApiResponse(code = 400, message = "if the request ID is not a valid UUID"),
            @ApiResponse(code = 403, message = "if the request is denied for security reasons"),
            @ApiResponse(code = 409, message = "if the device already exists"),
            @ApiResponse(code = 417, message = "if the user for whom the device should be created can't be found")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = RESTCodec.HEADER_REQUEST_ID, value = "ID of the request", required = false,
                    dataType = "uuid", paramType = "header")
    })
    public Response createDevice(@PathParam("username") String username, Map json) throws URISyntaxException {
        String name = (String) json.get("name");
        String type = (String) json.get("type");
        checkArgument(name != null, "Missing device name");
        checkArgument(type != null, "Missing device type");
        Optional<User> optionalOwner = usersService.findByUsername(username);
        if (!optionalOwner.isPresent()) {
            return Response
                    .status(EXPECTATION_FAILED)
                    .entity(Errors.withErrors(new ErrorMessage("Can't find user '%s'", username)))
                    .build();
        }
        User owner = optionalOwner.get();
        Device device = devicesService.create(owner, name, type);
        if (!devicesService.findDeviceById(device.getId()).isPresent()) {
            devicesService.saveDevice(device);
            URI deviceURI = new URI(UriTemplateUtils.createURI(
                    "/{username}/devices/{device}",
                    username, device.getName()));
            return Response
                    .created(deviceURI)
                    .entity(device)
                    .build();
        } else {
            return Response
                    .status(CONFLICT)
                    .entity(Errors.withErrors(
                            new ErrorMessage("The device '%s' for user '%s' already exists. " +
                                    "Use PUT method on the resource instead!",
                                    device.getName(), device.getOwner())))
                    .build();
        }
    }

    @PUT
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @RequiresPermissions(Permissions.DEVICES_CREATE)
//    @ApiOperation(value = "Update a device",
//            response = Device.class,
//            authorizations = @Authorization(value = "api_key", type = "api_key"))
//    @ApiResponses({
//            @ApiResponse(code = 201, message = "the created device"),
//            @ApiResponse(code = 400, message = "if the request ID is not a valid UUID"),
//            @ApiResponse(code = 403, message = "if the request is denied for security reasons"),
//            @ApiResponse(code = 409, message = "if the device already exists"),
//            @ApiResponse(code = 417, message = "if the user for whom the device should be created can't be found")
//    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = RESTCodec.HEADER_REQUEST_ID, value = "ID of the request", required = false,
                    dataType = "uuid", paramType = "header")
    })
    public Response updateDevice(@PathParam("username") String username, @PathParam("name") String name, Map json)
            throws URISyntaxException {
        String newName = (String) json.get("name");
        String newType = (String) json.get("type");
        checkArgument(newName != null, "Missing device name");
        checkArgument(newType != null, "Missing device type");

        // TODO: support update of device via JAX-RS
        throw new UnsupportedOperationException("Device update is now working yet!");
    }

    @PUT
    @Path("/{name}/configuration")
    @Consumes(MediaType.APPLICATION_JSON)
    @RequiresPermissions(Permissions.DEVICES_CONFIGURE)
    @ApiOperation(value = "Configure a device",
            response = Device.class,
            authorizations = @Authorization(value = "api_key", type = "api_key"))
    @ApiResponses({
            @ApiResponse(code = 200, message = "the configured device"),
            @ApiResponse(code = 400, message = "if the request ID is not a valid UUID"),
            @ApiResponse(code = 403, message = "if the request is denied for security reasons"),
            @ApiResponse(code = 404, message = "if the device can't be found")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = RESTCodec.HEADER_REQUEST_ID, value = "ID of the request", required = false,
                    dataType = "uuid", paramType = "header")
    })
    public Response configureDevice(
            @ApiParam(value = "the owner of the device", required = true) @PathParam("username") String username,
            @ApiParam(value = "the name of the device", required = true) @PathParam("name") String name,
            @ApiParam(value = "the configuration of the device to merge with the existing one", required = true) Map json)
            throws URISyntaxException {
        Optional<Device> optionalDevice = devicesService.findDeviceByName(username, name);
        if (!optionalDevice.isPresent()) {
            return Response
                    .status(EXPECTATION_FAILED)
                    .entity(Errors.withErrors(new ErrorMessage("Can't find device '%s'", name)))
                    .build();
        }
        Device device = devicesService.configure(optionalDevice.get().getId(), json);
        return Response
                .ok()
                .entity(device)
                .build();
    }

    @GET
    @Path("/supported")
    @RequiresPermissions(Permissions.DEVICES_SUPPORTED)
    @ApiOperation(value = "Find all supported devices",
            response = DeviceMetadata.class, responseContainer = "List",
            authorizations = @Authorization(value = "api_key", type = "api_key"))
    @ApiResponses({
            @ApiResponse(code = 200, message = "the list of devices metadata"),
            @ApiResponse(code = 400, message = "if the request ID is not a valid UUID"),
            @ApiResponse(code = 403, message = "if the request is denied for security reasons")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = RESTCodec.HEADER_REQUEST_ID, value = "ID of the request", required = false,
                    dataType = "uuid", paramType = "header")
    })
    public Response findAllSupportedDevices() {
        List<DeviceMetadata> devicesMetadata = devicesService.findAllSupportedDevices()
                .toList().toBlocking().single();
        return Response
                .ok(devicesMetadata)
                .header(CACHE_CONTROL, "max-age=3600, must-revalidate")
                .build();
    }
}
