package com.kalixia.ha.devices.gce.ecodevices;

import com.kalixia.ha.devices.gce.ecodevices.commands.FetchTeleinfoCountersCommand;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.capabilities.Counter;
import com.kalixia.ha.model.devices.AbstractDevice;
import com.kalixia.ha.model.devices.PullBasedDevice;
import com.kalixia.ha.model.sensors.CounterSensor;
import rx.Observable;

import javax.measure.unit.NonSI;
import java.util.UUID;

/**
 * An EcoDevice is made of 4 counters.
 * The first two ones are usually W/h counters
 * -- they are in facts two counters in one (if you have different prices during the day).
 * The third one is usually metering water usage (L).
 * The last one is usally metering gaz.
 * The last two counters (water and gaz) can easily be used for anything else as they only meter impulsions.
 *
 * The gaz counter is not yet implemented.
 * TODO: add support for the gaz counter
 */
public class EcoDevice extends AbstractDevice<EcoDeviceConfiguration> implements PullBasedDevice {
    private Teleinfo teleinfo1;
    private Teleinfo teleinfo2;
    private CounterSensor<?> water;
    private final TeleinfoRetriever retreiver;

    public EcoDevice(UUID id, String name, User owner) {
        super(id, name, owner, Counter.class);
        teleinfo1 = new Teleinfo(Teleinfo.TeleinfoName.TELEINFO1);
        teleinfo2 = new Teleinfo(Teleinfo.TeleinfoName.TELEINFO2);
        water = new CounterSensor<>("Water", NonSI.LITRE);
        this.retreiver = new EcoDeviceTeleinfoRetriever();
        addSensors(teleinfo1.getSensors());
        addSensors(teleinfo2.getSensors());
        addSensor(water);
    }

    @Override
    public void init(EcoDeviceConfiguration configuration) {
        this.configuration = configuration;
        LOGGER.info("Eco Device initialized");
    }

    @Override
    public void fetchSensorsData() {
        FetchTeleinfoCountersCommand teleinfoCommand1 = new FetchTeleinfoCountersCommand(teleinfo1, configuration, retreiver);
        FetchTeleinfoCountersCommand teleinfoCommand2 = new FetchTeleinfoCountersCommand(teleinfo2, configuration, retreiver);
        Observable<Long> teleinfo1Values = teleinfoCommand1.observe().dematerialize(); // TODO: make sure dematerialize is what's needed
        Observable<Long> teleinfo2Values = teleinfoCommand2.observe().dematerialize(); // TODO: make sure dematerialize is what's needed
    }

    @Override
    protected String getConfigurationFilename() {
        return "gce-eco-device";
    }

    @Override
    protected Class getConfigurationClass() {
        return EcoDeviceConfiguration.class;
    }
}
