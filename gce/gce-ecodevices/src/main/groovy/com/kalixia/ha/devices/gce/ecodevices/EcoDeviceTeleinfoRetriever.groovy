package com.kalixia.ha.devices.gce.ecodevices

import com.google.common.base.Strings
import com.kalixia.ha.model.configuration.AuthenticationConfiguration
import org.w3c.dom.Document
import rx.Observable
import rx.Observer

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import static com.kalixia.ha.devices.gce.ecodevices.Teleinfo.TeleinfoName.TELEINFO1
import static com.kalixia.ha.devices.gce.ecodevices.Teleinfo.TeleinfoName.TELEINFO2

class EcoDeviceTeleinfoRetriever implements TeleinfoRetriever {
    @Override
    Observable<Long> retrieveIndexes(Teleinfo teleinfo, EcoDeviceConfiguration configuration) {
        URL xmlUrl = null;
        switch (teleinfo.getName()) {
            case TELEINFO1:
                xmlUrl = new URL(configuration.getUrl() + "/protect/settings/teleinfo1.xml");
                break;
            case TELEINFO2:
                xmlUrl = new URL(configuration.getUrl() + "/protect/settings/teleinfo2.xml");
                break;
        }

        final AuthenticationConfiguration ecoDeviceAuthentication = configuration.getAuthenticationConfiguration();
        if (!Strings.isNullOrEmpty(ecoDeviceAuthentication.getUsername())) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            ecoDeviceAuthentication.getUsername(),
                            ecoDeviceAuthentication.getPassword().toCharArray());
                }
            });
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlUrl.toURI().toString());
        doc.getDocumentElement().normalize();

        Long hp = 0L, hc = 0L;
        switch (teleinfo.getName()) {
            case TELEINFO1:
                hp = Long.parseLong(doc.getElementsByTagName("T1_HCHP").item(0).getTextContent());
                hc = Long.parseLong(doc.getElementsByTagName("T1_HCHC").item(0).getTextContent());
                break;
            case TELEINFO2:
                hp = Long.parseLong(doc.getElementsByTagName("T2_HCHP").item(0).getTextContent());
                hc = Long.parseLong(doc.getElementsByTagName("T2_HCHC").item(0).getTextContent());
                break;
        }

        return Observable.create({ Observer observer ->
            observer.onNext(hp)
            observer.onNext(hc)
            observer.onCompleted()
        })
    }
}
