package com.kalixia.ha.gateway.codecs.jaxrs.converters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Converters {
    private static Map<Class, Converter> convertersMap;

    static {
        List<? extends Converter> converters = Arrays.asList(
            new UUIDConverter()
        );
        convertersMap = new HashMap<Class, Converter>();
        for (Converter converter : converters) {
            List<Class> acceptedClasses = converter.acceptClasses();
            for (Class clazz : acceptedClasses) {
                convertersMap.put(clazz, converter);
            }
        }
    }

    public static <T> T fromString(Class<T> clazz, String str) throws ConverterNotFoundException {
        Converter<T> converter = convertersMap.get(clazz);
        if (converter == null) {
            throw new ConverterNotFoundException(clazz);
        }
        return converter.fromString(str);
    }
}
