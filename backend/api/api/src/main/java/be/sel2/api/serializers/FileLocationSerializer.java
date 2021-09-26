package be.sel2.api.serializers;

import be.sel2.api.util.StaticFilePathBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Serializer that build the file serve location based on the actual value (filename)
 */
public class FileLocationSerializer extends StdSerializer<String> {

    public FileLocationSerializer() {
        this(null);
    }

    protected FileLocationSerializer(Class<String> t) {
        super(t);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        String translatedValue = StaticFilePathBuilder.buildServePath(value);
        provider.defaultSerializeValue(translatedValue, gen);
    }
}
