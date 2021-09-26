package be.sel2.api.serializers;

import be.sel2.api.models.BooleanModel;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class BooleanModelSerializer extends StdSerializer<BooleanModel> {

    public BooleanModelSerializer() {
        this(null);
    }

    protected BooleanModelSerializer(Class<BooleanModel> t) {
        super(t);
    }

    @Override
    public void serialize(BooleanModel value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue(value.getValue(), gen);
    }
}
