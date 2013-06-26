package com.kalixia.ha.api.rest.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import javax.measure.unit.Unit;

public class JScienceModule extends SimpleModule {
    public JScienceModule() {
        // deserializers
        // serializers
        addSerializer(Unit.class, new UnitSerializer());
    }
}
