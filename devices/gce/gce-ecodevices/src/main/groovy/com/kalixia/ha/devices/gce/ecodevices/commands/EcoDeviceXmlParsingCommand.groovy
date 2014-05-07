package com.kalixia.ha.devices.gce.ecodevices.commands

import com.kalixia.ha.devices.gce.ecodevices.EcoDeviceConfiguration
import com.kalixia.ha.devices.gce.ecodevices.TeleinfoSensor
import com.kalixia.ha.model.quantity.WattsPerHour
import com.netflix.hystrix.HystrixCommand
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandKey
import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder

import javax.measure.Measurable
import javax.measure.Measure
import javax.measure.quantity.Quantity
import javax.measure.unit.SI

import static com.kalixia.ha.devices.gce.ecodevices.TeleinfoSensorSlot.TELEINFO1
import static com.kalixia.ha.devices.gce.ecodevices.TeleinfoSensorSlot.TELEINFO2

@Slf4j("LOGGER")
class EcoDeviceXmlParsingCommand extends HystrixCommand<List<Measurable<Quantity>>> {
    private final TeleinfoSensor teleinfo
    private final EcoDeviceConfiguration configuration

    public EcoDeviceXmlParsingCommand(TeleinfoSensor teleinfo, EcoDeviceConfiguration configuration) {
        super(HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("EcoDevice"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("Teleinfo"))
        )
        super.executionTimeInMilliseconds
        this.teleinfo = teleinfo
        this.configuration = configuration
    }

    @Override
    protected List<Measurable<Quantity>> run() throws Exception {
        def http = new HTTPBuilder(configuration.url)
        if (configuration.authentication != null) {
            http.auth.basic configuration.authentication.username,
                    configuration.authentication.password
        }

        LOGGER.info("About to make HTTP call to ${configuration.url}/protect/settings/${teleinfo.slot.slug}")

        List<Measurable<Quantity>> values = []
        http.get(path: "/protect/settings/${teleinfo.slot.slug}") { resp, xml ->
            switch (teleinfo.slot) {
                case TELEINFO1:
                    values << Measure.valueOf(xml.T1_PPAP.text() as Long, SI.WATT)
                    values << Measure.valueOf(xml.T1_HCHP.text() as Long, WattsPerHour.UNIT)
                    values << Measure.valueOf(xml.T1_HCHC.text() as Long, WattsPerHour.UNIT)
                    break
                case TELEINFO2:
                    values << Measure.valueOf(xml.T2_PPAP.text() as Long, SI.WATT)
                    values << Measure.valueOf(xml.T2_HCHP.text() as Long, WattsPerHour.UNIT)
                    values << Measure.valueOf(xml.T2_HCHC.text() as Long, WattsPerHour.UNIT)
                    break
            }
        }

        http.shutdown()

        LOGGER.info("Indexes: instant power=${values[0]}, HP=${values[1]}, HC=${values[2]}")
        return values
    }
}
