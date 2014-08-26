package com.kalixia.ha.model.sensors;

import com.kalixia.ha.model.devices.Device;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import static java.lang.String.format;
import static com.google.common.base.Preconditions.checkNotNull;

public class SensorBuilder<D extends Device> {
    private D device;
    private String name;
    private String type;
    private Unit<? extends Quantity> unit;
    private DataPoint<? extends Quantity> dataPoint;
    private final Map<String, SensorCreator> sensorsCreatorsMap;

    public SensorBuilder() {
        sensorsCreatorsMap = new HashMap<>();
        ServiceLoader.load(SensorCreator.class)
                .forEach(creator -> sensorsCreatorsMap.put(creator.getDeviceType(), creator));
    }

    public SensorBuilder<D> forDevice(D device) {
        this.device = device;
        return this;
    }

    public SensorBuilder<D> ofType(String type) {
        this.type = type;
        return this;
    }

    public SensorBuilder<D> withName(String name) {
        this.name = name;
        return this;
    }

    public SensorBuilder<D> withUnit(Unit<? extends Quantity> unit) {
        this.unit = unit;
        return this;
    }

    public SensorBuilder<D> withLastValue(DataPoint<? extends Quantity> dataPoint) {
        this.dataPoint = dataPoint;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <Q extends Quantity> Sensor<Q> build() {
        checkNotNull(device, "device can't be null");
        String deviceType = device.getType();
        SensorCreator sensorCreator = sensorsCreatorsMap.get(deviceType);
        if (sensorCreator == null)
            throw new IllegalArgumentException(format("Can't build sensor for device %s", deviceType));
        return sensorCreator.createFromBuilder(this);
    }

    public D getDevice() {
        return device;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Unit<? extends Quantity> getUnit() {
        return unit;
    }

    public DataPoint<? extends Quantity> getLastValue() {
        return dataPoint;
    }
}
