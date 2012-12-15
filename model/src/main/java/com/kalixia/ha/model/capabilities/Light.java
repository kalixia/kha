package com.kalixia.ha.model.capabilities;

import com.kalixia.ha.model.Capability;

/**
 * Capability for controlling light.
 */
public interface Light extends Capability {
    void setColor(Color color);
    Color getColor();

    public class Color {
        private final float hue, saturation, value;

        public Color(float hue, float saturation, float value) {
            this.hue = hue;
            this.saturation = saturation;
            this.value = value;
        }

        public float getHue() {
            return hue;
        }

        public float getSaturation() {
            return saturation;
        }

        public float getValue() {
            return value;
        }
    }
}
