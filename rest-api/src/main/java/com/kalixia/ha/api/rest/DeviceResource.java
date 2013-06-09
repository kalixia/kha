package com.kalixia.ha.api.rest;

import com.kalixia.ha.api.DevicesFactory;
import com.kalixia.ha.api.DevicesService;
import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.model.Device;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.RGBLamp;
import com.kalixia.rawsag.codecs.jaxrs.UriTemplateUtils;
import javax.inject.Inject;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

@Path("/{username}/devices")
@Produces(MediaType.APPLICATION_JSON)
public class DeviceResource {
    @Inject
    DevicesService devicesService;

    @Inject
    UsersService usersService;

    @GET
    public @NotNull List<? extends Device> findAllDevicesOfUser(@PathParam("username") String username) {
        return devicesService.findAllDevicesOfUser(username).toList().toBlockingObservable().single();
    }

    @GET
    @Path("{id}")
    public @NotNull Device findDeviceById(@PathParam("id") UUID id) {
        return devicesService.findDeviceById(id).toBlockingObservable().single();
    }

    /**
     * Create a {@link Device} for a given {@link User} identified by it's <tt>username</tt>.
     * <p>
     * <tt>curl -i -H "Accept: application/json" -X POST -d "{name: 'test', type: 'RGBLamp' }" http://localhost:8082/jeje/devices</tt>
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDevice(@PathParam("username") String username, Map json) throws URISyntaxException {
        String name = (String) json.get("name");
        String type = (String) json.get("type");
        checkArgument(name != null, "Missing device name");
        checkArgument(type != null, "Missing device type");
        if (!"RGBLamp".equals(type))
            throw new IllegalArgumentException(String.format("Invalid device type %s", type));

        User owner = usersService.findByUsername(username);
        if (owner == null) {
            return Response.status(Response.Status.EXPECTATION_FAILED)
                    .entity(String.format("Can't find user %s", username)).build();
        }

        Device device = DevicesFactory.createDevice(name, owner, RGBLamp.class);
        devicesService.saveDevice(device);

        URI deviceURI = new URI(UriTemplateUtils.createURI(
                "/{username}/devices/{device}",
                username, device.getId().toString()));
        return Response.created(deviceURI).build();
    }

}
