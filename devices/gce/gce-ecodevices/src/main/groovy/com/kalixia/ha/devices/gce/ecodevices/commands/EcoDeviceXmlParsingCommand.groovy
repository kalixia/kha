package com.kalixia.ha.devices.gce.ecodevices.commands

import com.kalixia.ha.devices.gce.ecodevices.EcoDeviceConfiguration
import com.kalixia.ha.devices.gce.ecodevices.TeleinfoSensor
import com.kalixia.ha.model.quantity.WattsPerHour
import com.netflix.hystrix.HystrixCommand
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandKey
import groovy.util.logging.Slf4j
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.reactivex.netty.protocol.http.client.HttpClient
import io.reactivex.netty.protocol.http.client.HttpClientRequest
import io.reactivex.netty.protocol.http.client.HttpClientResponse
import rx.functions.Func1

import javax.measure.Measurable
import javax.measure.Measure
import javax.measure.quantity.Quantity
import javax.measure.unit.SI
import java.nio.charset.Charset

import static com.kalixia.ha.devices.gce.ecodevices.TeleinfoSensorSlot.TELEINFO1
import static com.kalixia.ha.devices.gce.ecodevices.TeleinfoSensorSlot.TELEINFO2
import static io.netty.handler.codec.http.HttpHeaders.Names.AUTHORIZATION
import static io.netty.util.CharsetUtil.UTF_8

@Slf4j("LOGGER")
class EcoDeviceXmlParsingCommand extends HystrixCommand<List<Measurable<Quantity>>> {
    private final TeleinfoSensor teleinfo
    private final EcoDeviceConfiguration configuration
    private final HttpClient<ByteBuf, ByteBuf> httpClient
    private final String requestURL

    public EcoDeviceXmlParsingCommand(TeleinfoSensor teleinfo, EcoDeviceConfiguration configuration,
                                      HttpClient<ByteBuf, ByteBuf> httpClient) {
        super(HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("EcoDevice"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("Teleinfo"))
        )
        super.executionTimeInMilliseconds
        this.teleinfo = teleinfo
        this.configuration = configuration
        this.httpClient = httpClient
        this.requestURL = "/protect/settings/${teleinfo.slot.slug}"
    }

    @Override
    protected List<Measurable<Quantity>> run() throws Exception {
        HttpClientRequest<ByteBuf> request = HttpClientRequest.createGet(requestURL)
        if (configuration.authentication != null) {
            String authString = configuration.authentication.username + ":" + configuration.authentication.password
            ByteBuf authChannelBuffer = Unpooled.copiedBuffer(authString, UTF_8)
            ByteBuf encodedAuthChannelBuffer = io.netty.handler.codec.base64.Base64.encode(authChannelBuffer)
            request.withHeader(AUTHORIZATION, "Basic: " + encodedAuthChannelBuffer.toString(UTF_8));
        }

        List<Measurable<Quantity>> values = httpClient.submit(request)
                .flatMap({ HttpClientResponse<ByteBuf> response -> response.getContent() } as Func1)
                .map({ ByteBuf data -> data.toString(Charset.defaultCharset()) } as Func1)
                .flatMap({ String content ->
                    def xml = new XmlParser().parseText(content)
                    def List<Measurable<Quantity>> values = []
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
                    return rx.Observable.from(values)
                } as Func1)
                .toList()
                .toBlocking().single()

        LOGGER.info("Indexes: instant power=${values[0]}, HP=${values[1]}, HC=${values[2]}")
        return values
    }
}
