package com.kalixia.netty.rest.codecs.jaxrs.converters;

import java.util.List;

/**
 * A converter eases conversion from/to {@link String}.
 */
public interface Converter<T> {
    List<Class<T>> acceptClasses();
    String asString(T obj);
    T fromString(String str);
}
