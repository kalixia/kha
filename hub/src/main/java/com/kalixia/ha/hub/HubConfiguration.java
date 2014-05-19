package com.kalixia.ha.hub;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalixia.ha.api.configuration.StorageConfiguration;
import com.kalixia.ha.model.configuration.Configuration;

public class HubConfiguration extends Configuration {
    @JsonProperty("storage")
    private StorageConfiguration storage;

    public StorageConfiguration getStorage() {
        return storage;
    }
}
