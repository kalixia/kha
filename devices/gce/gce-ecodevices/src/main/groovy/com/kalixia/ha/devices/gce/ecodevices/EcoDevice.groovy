package com.kalixia.ha.devices.gce.ecodevices

import com.kalixia.ha.model.User
import com.kalixia.ha.model.capabilities.Counter
import com.kalixia.ha.model.devices.AbstractDevice
import com.kalixia.ha.model.devices.DeviceBuilder
import com.kalixia.ha.model.devices.PullBasedDevice
import com.kalixia.ha.model.quantity.WattsPerHour
import com.kalixia.ha.model.sensors.CounterSensor
import io.reactivex.netty.RxNetty
import io.reactivex.netty.protocol.http.client.HttpClient
import rx.Observable

import javax.measure.Measurable
import javax.measure.quantity.Volume
import javax.measure.unit.NonSI
import javax.measure.unit.SI

/**
 * An EcoDevice is made of 4 counters.
 * The first two ones are usually W/h counters
 * -- they are in facts two counters in one (if you have different prices during the day).
 * The third one is usually metering water usage (L).
 * The last one is usually metering gaz usage (m3).
 * The last two counters (water and gaz) can easily be used for anything else as they only meter impulses.
 *
 * The gaz and water counters are not yet implemented.
 */
 class EcoDevice extends AbstractDevice<EcoDeviceConfiguration> implements PullBasedDevice {
    private final TeleinfoSensor teleinfoSensor1
    private final TeleinfoSensor teleinfoSensor2
    private final CounterSensor<Volume> counter1
    private final CounterSensor<Volume> counter2
    private TeleinfoRetriever retriever
    private HttpClient httpClient
    public static final String TYPE = "gce-eco-device"

    def EcoDevice(DeviceBuilder builder) {
        super(builder, Counter.class)
        teleinfoSensor1 = new TeleinfoSensor(configuration.power1.name, TeleinfoSensorSlot.TELEINFO1)
        teleinfoSensor2 = new TeleinfoSensor(configuration.power2.name, TeleinfoSensorSlot.TELEINFO2)
        counter1 = new CounterSensor<>(configuration.counter1.name, NonSI.LITRE)
        counter2 = new CounterSensor<>(configuration.counter2.name, SI.CUBIC_METRE)
        addSensors(teleinfoSensor1, teleinfoSensor2, counter1, counter2)
    }

    @Override
    void init(EcoDeviceConfiguration configuration) {
        this.configuration = configuration
        this.httpClient = RxNetty.createHttpClient(configuration.getHost(), configuration.getPort())
        this.retriever = new EcoDeviceTeleinfoRetriever(httpClient)
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

    @Override
    String getType() {
        return TYPE
    }
}
