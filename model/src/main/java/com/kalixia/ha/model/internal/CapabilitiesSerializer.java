package com.kalixia.ha.model.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.capabilities.Capability;
import java.io.IOException;
import java.util.Set;

/**
 * Serializer for {@link com.kalixia.ha.model.User}s.
 * Only serializes the <tt>username</tt> property of the {@link com.kalixia.ha.model.User} as an unwrappred property.
 */
public class CapabilitiesSerializer extends JsonSerializer<Set<Class<? extends Capability>>> {
    @Override
    public void serialize(Set<Class<? extends Capability>> capabilities, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException {
        jgen.writeStartArray();
        for (Class<? extends Capability> capability : capabilities) {
            jgen.writeString(capability.getSimpleName());
        }
        jgen.writeEndArray();
    }
}
