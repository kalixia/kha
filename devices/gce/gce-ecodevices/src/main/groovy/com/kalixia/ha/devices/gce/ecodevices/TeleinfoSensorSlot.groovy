package com.kalixia.ha.devices.gce.ecodevices

enum TeleinfoSensorSlot {
    TELEINFO1("teleinfo1.xml"), TELEINFO2("teleinfo2.xml")

    private final String slug

    TeleinfoSensorSlot(String slug) {
        this.slug = slug
    }

    public String getSlug() {
        return slug
    }
}