package com.kalixia.ha.api;

import com.kalixia.ha.model.configuration.Configuration;
import com.kalixia.ha.model.configuration.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public abstract class Service<C extends Configuration> {
    protected C configuration;
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected abstract String getName();
    protected abstract Class<C> getConfigurationClass();

    public void init() throws IOException {
        this.configuration = ConfigurationBuilder.loadConfiguration(
                getName(), getConfigurationFilename(), getConfigurationClass());
    }

    public C getConfiguration() {
        return configuration;
    }

    protected String getConfigurationFilename() {
        return getName();
    }

}
