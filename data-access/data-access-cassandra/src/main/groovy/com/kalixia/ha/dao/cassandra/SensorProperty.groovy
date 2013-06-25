package com.kalixia.ha.dao.cassandra

import com.netflix.astyanax.annotations.Component

/**
 * Used by {@link com.kalixia.ha.model.sensors.Sensor}s for composite columns.
 */
class SensorProperty {
    @Component(ordinal = 0) String sensor
    @Component(ordinal = 1) String property

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        SensorProperty that = (SensorProperty) o

        if (property != that.property) return false
        if (sensor != that.sensor) return false

        return true
    }

    int hashCode() {
        int result
        result = (sensor != null ? sensor.hashCode() : 0)
        result = 31 * result + (property != null ? property.hashCode() : 0)
        return result
    }
}
