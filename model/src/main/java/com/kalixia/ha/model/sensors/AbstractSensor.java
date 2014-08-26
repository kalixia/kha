package com.kalixia.ha.model.sensors;

import javax.measure.quantity.Quantity;
import java.util.Objects;

import static com.google.common.base.Objects.toStringHelper;

/**
 * Base class for sensors providing support for inclusions into {@link java.util.Map}s or {@link java.util.Set}s.
 * <p>
 * It is highly recommended to have sensors to extends this base class!
 *
 * @param <Q> the type of quantity the sensor is about
 */
public abstract class AbstractSensor<Q extends Quantity> implements Sensor<Q> {

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass().equals(obj.getClass())) {
            Sensor other = (Sensor) obj;
            return Objects.equals(getName(), other.getName())
                    && Objects.equals(getType(), other.getType());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getType());
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("name", getName())
                .add("type", getType())
                .toString();
    }

}
