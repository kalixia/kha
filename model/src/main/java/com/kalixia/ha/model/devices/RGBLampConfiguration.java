package com.kalixia.ha.model.devices;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalixia.ha.model.configuration.Configuration;

public class RGBLampConfiguration extends Configuration {
    @JsonProperty("intensity")
    private float intensity;

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
