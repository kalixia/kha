package com.kalixia.ha.api.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalixia.ha.model.configuration.Configuration;

public class UsersServiceConfiguration extends Configuration {
    @JsonProperty("storage")
    private StorageConfiguration storage;

    public StorageConfiguration getStorage() {
        return storage;
    }
}
