package com.kalixia.ha.api.rest.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import javax.measure.unit.Unit;
import java.io.IOException;

public class UnitSerializer extends StdSerializer<Unit> {
    protected UnitSerializer() {
        super(Unit.class);
    }

    @Override
    public void serialize(Unit unit, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeString(unit.toString());
    }
}
