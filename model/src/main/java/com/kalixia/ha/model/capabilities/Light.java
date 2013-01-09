package com.kalixia.ha.model.capabilities;

import com.kalixia.ha.model.Capability;
import com.kalixia.ha.model.Color;

/**
 * Capability for controlling light.
 */
public interface Light extends Capability {
    void setColor(Color color);
    Color getColor();
}
