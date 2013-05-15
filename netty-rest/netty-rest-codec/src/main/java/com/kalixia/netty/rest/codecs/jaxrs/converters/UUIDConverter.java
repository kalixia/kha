package com.kalixia.netty.rest.codecs.jaxrs.converters;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UUIDConverter implements Converter<UUID> {

    @Override
    public List<Class<UUID>> acceptClasses() {
        return Arrays.asList(UUID.class);
    }

    @Override
    public String asString(UUID uuid) {
        return uuid.toString();
    }

    @Override
    public UUID fromString(String str) {
        return UUID.fromString(str);
    }

}
