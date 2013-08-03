package com.kalixia.ha.devices.gce.ecodevices

import com.kalixia.ha.model.User
import com.kalixia.ha.model.capabilities.Counter
import com.kalixia.ha.model.devices.AbstractDevice
import com.kalixia.ha.model.devices.PullBasedDevice
import com.kalixia.ha.model.quantity.WattsPerHour
import com.kalixia.ha.model.sensors.CounterSensor
import rx.Observable

import javax.measure.Measurable
import javax.measure.quantity.Volume
import javax.measure.unit.NonSI
import javax.measure.unit.SI

/**
 * An EcoDeviceOld is made of 4 counters.
 * The first two ones are usually W/h counters
 * -- they are in facts two counters in one (if you have different prices during the day).
 * The third one is usually metering counter1 usage (L).
 * The last one is usually metering gaz.
 * The last two counters (counter1 and gaz) can easily be used for anything else as they only meter impulsions.
 *
 * The gaz counter is not yet implemented.
 */
class EcoDevice extends AbstractDevice<EcoDeviceConfiguration> implements PullBasedDevice {
    private final TeleinfoSensor teleinfoSensor1
    private final TeleinfoSensor teleinfoSensor2
    private final CounterSensor<Volume> counter1
    private final CounterSensor<Volume> counter2
    private final TeleinfoRetriever retriever

    def EcoDevice(UUID id, String name, User owner) {
        super(id, name, owner, Counter.class);
        teleinfoSensor1 = new TeleinfoSensor(configuration.power1.name, TeleinfoSensorSlot.TELEINFO1)
        teleinfoSensor2 = new TeleinfoSensor(configuration.power2.name, TeleinfoSensorSlot.TELEINFO2)
        counter1 = new CounterSensor<>(configuration.counter1.name, NonSI.LITRE)
        counter2 = new CounterSensor<>(configuration.counter2.name, SI.CUBIC_METRE)
        this.retriever = new EcoDeviceTeleinfoRetriever()
        addSensors(teleinfoSensor1, teleinfoSensor2, counter1)
    }

    @Override
    void init(EcoDeviceConfiguration configuration) {
        this.configuration = configuration
        LOGGER.info("Eco Device initialized")
    }

    @Override
    void fetchSensorsData() {
        Observable<Measurable<WattsPerHour>> teleinfoObservable1 =
                retriever.retrieveIndexes(teleinfoSensor1, configuration)
        Observable<Measurable<WattsPerHour>> teleinfoObservable2 =
                retriever.retrieveIndexes(teleinfoSensor2, configuration);
        // TODO: retrieve counter1 and counter2 counters when the EcoDevice firmware is updated!
    }

    @Override
    protected String getConfigurationFilename() {
        return "gce-eco-device"
    }

    @Override
    protected Class<EcoDeviceConfiguration> getConfigurationClass() {
        return EcoDeviceConfiguration.class
    }
}
