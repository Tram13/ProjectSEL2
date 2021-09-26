package be.sel2.api.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Serializer specialized in writing Enum values in lowercase
 */
public class EnumToLowercaseSerializer<X extends Enum<?>> extends StdSerializer<X> {

    public EnumToLowercaseSerializer() {
        this(null);
    }

    protected EnumToLowercaseSerializer(Class<X> t) {
        super(t);
    }

    @Override
    public void serialize(X value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        String name = value.toString();

        provider.defaultSerializeValue(name.toLowerCase(), gen);
    }
}
