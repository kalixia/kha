package com.kalixia.ha.model.devices;

import com.kalixia.ha.model.Capability;
import com.kalixia.ha.model.capabilities.Light;
import com.kalixia.ha.model.capabilities.Switch;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Devices which controls a RGB lamp or RGB Led Strip.
 * Has both {@link Switch} and {@link Light} capabilities.
 */
public class RGBLamp extends AbstractDevice implements Light, Switch {
    private Boolean on;
    private Color color;

    @SuppressWarnings("unchecked")
    public RGBLamp(UUID id, String name) {
        super(id, name, Switch.class, Light.class);
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void open() {
        on = true;
    }

    @Override
    public void close() {
        on = false;
    }

    @Override
    public Status getStatus() {
        return on ? Status.OPENED : Status.CLOSED;
    }

}
