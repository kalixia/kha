package com.kalixia.ha.api.environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.Reader;

@Singleton
public class ConfigurationBuilder {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public static <T> T fromStream(Reader reader, Class<T> clazz) throws IOException {
        return mapper.readValue(reader, clazz);
    }
}
