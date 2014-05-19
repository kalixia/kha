package com.kalixia.ha.model.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

@Singleton
public class ConfigurationBuilder {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationBuilder.class);

    public static <C extends Configuration> C loadConfiguration(String name,
                                                                String configurationFilenameWithoutExt,
                                                                Class<C> configurationClass)
            throws IOException {
        File serviceConfigurationFile = new File(
                System.getProperty("app.home") + "/etc/", configurationFilenameWithoutExt + ".yml");
        if (serviceConfigurationFile.exists()) {
            return loadConfigurationFromStream(configurationClass, new FileInputStream(serviceConfigurationFile));
        } else {
            String serviceConfigurationFilename = configurationClass.getPackage().getName().replace('.', '/') + '/' + configurationFilenameWithoutExt + ".yml";
            InputStream serviceConfigurationStream = configurationClass.getClassLoader().getResourceAsStream(serviceConfigurationFilename);
            C conf = loadConfigurationFromStream(configurationClass, serviceConfigurationStream);
            if (conf == null)
                LOGGER.error("Can't find configuration file for '{}'", name, configurationFilenameWithoutExt);
            return conf;
        }
    }

    private static <C extends Configuration> C loadConfigurationFromStream(Class<C> configurationClass,
                                                                           InputStream stream)
            throws IOException {
        try {
            if (stream == null) {
                return null;
            } else {
                Reader serviceConfigurationReader = new InputStreamReader(stream);
                return ConfigurationBuilder.fromStream(serviceConfigurationReader, configurationClass);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    public static <T> T fromStream(Reader reader, Class<T> clazz) throws IOException {
        return mapper.readValue(reader, clazz);
    }
}
