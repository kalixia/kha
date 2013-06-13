package com.kalixia.ha.model.sensors;

import com.google.common.base.Objects;
import org.joda.time.Instant;

import java.io.Serializable;

/**
 * The main purpose of this class is to encapsulate both the value of a sensor and the time when the measurement was
 * done.
 * @param <T> the type of value
 */
public class DataPoint<T> implements Serializable {
    private final T value;
    private final Instant at;

    public DataPoint(T value, Instant at) {
        this.value = value;
        this.at = at;
    }

    public T getValue() {
        return value;
    }

    public Instant getAt() {
        return at;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("value", value)
                .add("at", at)
                .toString();
    }
}
