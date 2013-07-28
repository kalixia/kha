package com.kalixia.ha.api;

import com.kalixia.ha.api.environment.Configuration;
import com.kalixia.ha.api.environment.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public abstract class Service<C extends Configuration> {
    private final C configuration;
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected abstract String getName();
    protected abstract Class<C> getConfigurationClass();

    public Service() throws IOException {
        String serviceConfigurationFile = getClass().getPackage().getName().replace('.', '/') + '/' + getName() + ".yml";
        InputStream serviceConfigurationStream = getClass().getClassLoader().getResourceAsStream(serviceConfigurationFile);
        if (serviceConfigurationStream == null) {
            LOGGER.warn("Can't find configuration file {} for service {}. Skipping configuration phase.",
                    serviceConfigurationFile, getName());
            this.configuration = null;
        } else {
            Reader serviceConfigurationReader = new InputStreamReader(serviceConfigurationStream);
            this.configuration = ConfigurationBuilder.fromStream(serviceConfigurationReader, getConfigurationClass());
        }
    }

    public C getConfiguration() {
        return configuration;
    }

}
