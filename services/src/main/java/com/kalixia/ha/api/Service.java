package com.kalixia.ha.api;

import com.kalixia.ha.api.environment.Configuration;
import com.kalixia.ha.api.environment.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public abstract class Service<C extends Configuration> {
    private C configuration;
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected abstract String getName();
    protected abstract Class<C> getConfigurationClass();

    public void init() throws IOException {
        this.configuration = loadConfiguration();
    }

    public C getConfiguration() {
        return configuration;
    }

    private C loadConfiguration() throws IOException {
        String serviceConfigurationFile = System.getProperty("HA_HOME") + "/conf/" + getName() + ".yml";
        C conf = loadConfigurationFromStream(new FileInputStream(serviceConfigurationFile), serviceConfigurationFile);

        if (conf != null)
            return conf;

        serviceConfigurationFile = getClass().getPackage().getName().replace('.', '/') + '/' + getName() + ".yml";
        InputStream serviceConfigurationStream = getClass().getClassLoader().getResourceAsStream(serviceConfigurationFile);
        return loadConfigurationFromStream(serviceConfigurationStream, serviceConfigurationFile);
    }

    private C loadConfigurationFromStream(InputStream stream, String serviceConfigurationFile) throws IOException {
        try {
            if (stream == null) {
                LOGGER.warn("Can't find configuration file {} for service {}. Skipping configuration phase.",
                        serviceConfigurationFile, getName());
                return null;
            } else {
                Reader serviceConfigurationReader = new InputStreamReader(stream);
                return ConfigurationBuilder.fromStream(serviceConfigurationReader, getConfigurationClass());
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

}
