package com.kalixia.netty.rest.codecs.jaxrs.converters;

public class ConverterNotFoundException extends Exception {
    public ConverterNotFoundException(Class clazz) {
        super(String.format("Can't find converter for '%s'", clazz));
    }
}
