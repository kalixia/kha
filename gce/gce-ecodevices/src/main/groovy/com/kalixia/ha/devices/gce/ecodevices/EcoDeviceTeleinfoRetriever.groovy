package com.kalixia.ha.devices.gce.ecodevices

import com.kalixia.ha.devices.gce.ecodevices.commands.EcoDeviceXmlParsingCommand
import com.kalixia.ha.model.quantity.WattsPerHour
import groovy.util.logging.Slf4j
import rx.Observable

import javax.measure.Measurable
import javax.measure.Measure

import static TeleinfoSensorSlot.TELEINFO1
import static TeleinfoSensorSlot.TELEINFO2

@Slf4j("LOGGER")
class EcoDeviceTeleinfoRetriever implements TeleinfoRetriever {
    private static final Measurable<WattsPerHour> ZERO_WATTS_PER_HOUR = Measure.valueOf(0L, WattsPerHour.UNIT)

    @Override
    Observable<Measurable<WattsPerHour>> retrieveIndexes(TeleinfoSensor teleinfo, EcoDeviceConfiguration configuration) {
        switch (teleinfo.slot) {
            case TELEINFO1:
                if (!configuration.power1.enabled)
                    return Observable.from(ZERO_WATTS_PER_HOUR, ZERO_WATTS_PER_HOUR)
                break;
            case TELEINFO2:
                if (!configuration.power2.enabled)
                    return Observable.from(ZERO_WATTS_PER_HOUR, ZERO_WATTS_PER_HOUR)
                break;
        }
        return new EcoDeviceXmlParsingCommand(teleinfo, configuration)
            .observe()
            .mapMany({ List<Measurable<WattsPerHour>> values ->
                LOGGER.info("Got value ${values}")
                if (values.size() != 2) {
                    return Observable.error(
                            new IllegalStateException("Should only have 2 values but got ${values.size()} instead"))
                }
                return Observable.from(values)
            })
    }
}
