package com.kalixia.ha.api.rest;

import com.kalixia.grapi.codecs.jaxrs.UriTemplateUtils;
import com.kalixia.ha.api.DevicesService;
import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.DeviceMetadata;
import com.kalixia.ha.model.security.Permissions;
import org.apache.shiro.authz.annotation.RequiresPermissions;

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

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.HttpHeaders.CACHE_CONTROL;

@Path("/{username}/devices")
@Produces(MediaType.APPLICATION_JSON)
public class DeviceResource {
    @Inject
    DevicesService devicesService;

    @Inject
    UsersService usersService;

    @GET
    @RequiresPermissions(Permissions.DEVICES_VIEW)
    public @NotNull Response findAllDevicesOfUser(@PathParam("username") String username) {
        List<? extends Device> devices = devicesService.findAllDevicesOfUser(username)
                .toList().toBlockingObservable().single();
        return Response
                .ok(devices)
                .link(UriTemplateUtils.createURI("/{username}", username), "owner")
                .header(CACHE_CONTROL, "max-age=60, must-revalidate")
                .build();
    }

    @GET
    @Path("{name}")
    @RequiresPermissions(Permissions.DEVICES_VIEW)
    public @NotNull Response findDeviceByName(@PathParam("username") String username, @PathParam("name") String name) {
        Device device = devicesService.findDeviceByName(username, name);
        if (device == null)
            throw new WebApplicationException(Response.Status.NOT_FOUND);
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
    public Response createDevice(@PathParam("username") String username, Map json) throws URISyntaxException {
        String name = (String) json.get("name");
        String type = (String) json.get("type");
        checkArgument(name != null, "Missing device name");
        checkArgument(type != null, "Missing device type");
        User owner = usersService.findByUsername(username);
        if (owner == null) {
            return Response
                    .status(Response.Status.EXPECTATION_FAILED)
                    .entity(Errors.withErrors(new ErrorMessage("Can't find user '%s'", username)))
                    .build();
        }
        Device device = devicesService.create(owner, name, type);
        if (devicesService.findDeviceById(device.getId()) == null) {
            devicesService.saveDevice(device);
            URI deviceURI = new URI(UriTemplateUtils.createURI(
                    "/{username}/devices/{device}",
                    username, device.getName()));
            return Response.created(deviceURI).build();
        } else {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(Errors.withErrors(
                            new ErrorMessage("The device '%s' for user '%s' already exists. " +
                                    "Use PUT method on the resource instead!",
                                    device.getName(), device.getOwner())))
                    .build();
        }
    }

    @PUT
    @Path("{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @RequiresPermissions(Permissions.DEVICES_CREATE)
    public Response updateDevice(@PathParam("username") String username, @PathParam("name") String name, Map json)
            throws URISyntaxException {
        String newName = (String) json.get("name");
        String newType = (String) json.get("type");
        checkArgument(newName != null, "Missing device name");
        checkArgument(newType != null, "Missing device type");

        // TODO: support update of device via JAX-RS
        throw new UnsupportedOperationException("Device update is now working yet!");
    }

    @GET
    @Path("/supported")
    public Response findAllSupportedDevices() {
        List<DeviceMetadata> devicesMetadata = devicesService.findAllSupportedDevices()
                .toList().toBlockingObservable().single();
        return Response
                .ok(devicesMetadata)
                .header(CACHE_CONTROL, "max-age=3600, must-revalidate")
                .build();
    }
}
