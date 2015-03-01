package com.kalixia.ha.devices.gce.ecodevices

import com.kalixia.ha.devices.gce.ecodevices.commands.EcoDeviceXmlParsingCommand
import com.kalixia.ha.model.quantity.WattsPerHour
import groovy.util.logging.Slf4j
import io.reactivex.netty.protocol.http.client.HttpClient
import rx.Observable
import rx.functions.Action1
import rx.functions.Func1

import javax.measure.Measurable
import javax.measure.Measure
import javax.measure.quantity.Power
import javax.measure.quantity.Quantity
import javax.measure.unit.SI

import static TeleinfoSensorSlot.TELEINFO1
import static TeleinfoSensorSlot.TELEINFO2

@Slf4j("LOGGER")
class EcoDeviceTeleinfoRetriever implements TeleinfoRetriever {
    private static final Measurable<Power> ZERO_WATT = Measure.valueOf(0L, SI.WATT)
    private static final Measurable<WattsPerHour> ZERO_WATTS_PER_HOUR = Measure.valueOf(0L, WattsPerHour.UNIT)
    private final HttpClient httpClient

    EcoDeviceTeleinfoRetriever(HttpClient httpClient) {
        this.httpClient = httpClient
    }

    @Override
    Observable<Measurable<Quantity>> retrieveIndexes(TeleinfoSensor teleinfo, EcoDeviceConfiguration configuration) {
        switch (teleinfo.slot) {
            case TELEINFO1:
                if (!configuration.power1.enabled)
                    return Observable.from(ZERO_WATT, ZERO_WATTS_PER_HOUR, ZERO_WATTS_PER_HOUR)
                break;
            case TELEINFO2:
                if (!configuration.power2.enabled)
                    return Observable.from(ZERO_WATT, ZERO_WATTS_PER_HOUR, ZERO_WATTS_PER_HOUR)
                break;
        }
        return new EcoDeviceXmlParsingCommand(teleinfo, configuration, httpClient)
            .observe()
            .flatMap({ List<Measurable<WattsPerHour>> values ->
                LOGGER.info("Got value ${values}")
                if (values.size() != 3) {
                    return Observable.error(
                            new IllegalStateException("Should only have 2 values but got ${values.size()} instead"))
                }
                return Observable.from(values)
            } as Func1)
            .doOnError({ Throwable t -> LOGGER.error "Unexpected error", t } as Action1)
    }
}
