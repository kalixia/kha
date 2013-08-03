package com.kalixia.ha.devices.gce.ecodevices

enum TeleinfoSensorSlot {
    TELEINFO1("Téléinfo 1", "teleinfo1.xml"), TELEINFO2("Téléinfo 2", "teleinfo2.xml")

    private final String name
    private final String slug

    TeleinfoSensorSlot(String name, String slug) {
        this.name = name
        this.slug = slug
    }

    public String getName() {
        return name
    }

    public String getSlug() {
        return slug
    }
}