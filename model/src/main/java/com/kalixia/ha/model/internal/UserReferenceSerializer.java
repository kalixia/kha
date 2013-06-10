package com.kalixia.ha.model.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.kalixia.ha.model.User;
import java.io.IOException;

/**
 * Serializer for {@link User}s.
 * Only serializes the <tt>username</tt> property of the {@link User} as an unwrappred property.
 */
public class UserReferenceSerializer extends JsonSerializer<User> {
    @Override
    public void serialize(User user, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(user.getUsername());
    }
}
