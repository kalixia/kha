package com.kalixia.ha.model.devices;

import com.kalixia.ha.model.Color;
import com.kalixia.ha.model.capabilities.Dimmer;
import com.kalixia.ha.model.capabilities.Light;
import com.kalixia.ha.model.capabilities.Switch;
import com.kalixia.ha.model.capabilities.Temperature;

import java.util.UUID;

/**
 * Devices which controls a RGB lamp or RGB Led Strip.
 * Has both {@link com.kalixia.ha.model.capabilities.Switch} and {@link Light} capabilities.
 */
public class RGBLamp extends AbstractDevice implements Dimmer, Switch, Temperature {
    private boolean on;
    private Color color;
    private float intensity;
    private float celsius;

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
    public void on() {
        on = true;
    }

    @Override
    public void off() {
        on = false;
    }

    @Override
    public Status getStatus() {
        return on ? Status.ON : Status.OFF;
    }

    @Override
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    @Override
    public float getIntensity() {
        return intensity;
    }

    public float getCelsius() {
        return celsius;
    }

    public void setCelsius(float celsius) {
        this.celsius = celsius;
    }
}
