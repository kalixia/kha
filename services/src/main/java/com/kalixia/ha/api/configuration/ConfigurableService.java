package com.kalixia.ha.api.configuration;

import com.kalixia.ha.model.configuration.Configuration;

public interface ConfigurableService<C extends Configuration> {
    C getConfiguration();
}
