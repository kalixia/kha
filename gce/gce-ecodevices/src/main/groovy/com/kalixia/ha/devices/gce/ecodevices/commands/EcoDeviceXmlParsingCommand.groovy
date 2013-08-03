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

import static com.kalixia.ha.devices.gce.ecodevices.TeleinfoSensorSlot.TELEINFO1
import static com.kalixia.ha.devices.gce.ecodevices.TeleinfoSensorSlot.TELEINFO2

@Slf4j("LOGGER")
class EcoDeviceXmlParsingCommand extends HystrixCommand<List<Measurable<WattsPerHour>>> {
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
    protected List<Measurable<WattsPerHour>> run() throws Exception {
        def http = new HTTPBuilder(configuration.url)
        if (configuration.authenticationConfiguration != null) {
            http.auth.basic configuration.authenticationConfiguration.username,
                    configuration.authenticationConfiguration.password
        }

        LOGGER.info("About to make HTTP call to ${configuration.url}/protect/settings/${teleinfo.slot.slug}")

        List<Measurable<WattsPerHour>> indexes = []
        http.get(path: "/protect/settings/${teleinfo.slot.slug}") { resp, xml ->
            switch (teleinfo.slot) {
                case TELEINFO1:
                    indexes << Measure.valueOf(xml.T1_HCHP.text() as Long, WattsPerHour.UNIT)
                    indexes << Measure.valueOf(xml.T1_HCHC.text() as Long, WattsPerHour.UNIT)
                    break
                case TELEINFO2:
                    indexes << Measure.valueOf(xml.T2_HCHP.text() as Long, WattsPerHour.UNIT)
                    indexes << Measure.valueOf(xml.T2_HCHC.text() as Long, WattsPerHour.UNIT)
                    break
            }
        }

        http.shutdown()

        LOGGER.info("Indexes: HP=${indexes[0]}, HC=${indexes[1]}")
        return indexes
    }
}
