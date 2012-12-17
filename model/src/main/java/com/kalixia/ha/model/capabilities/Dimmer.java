package com.kalixia.ha.model.capabilities;

/**
 * Usually for lights, allow to progressively turn on the light.
 * The <tt>intensity</tt> is a value expected between 0 and 1.
 */
public interface Dimmer extends Light {
    void setIntensity(float intensity);
    float getIntensity();
}
