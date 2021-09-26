package be.sel2.api.serializers;

import be.sel2.api.models.BooleanModel;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class BooleanModelDeserializer extends StdDeserializer<BooleanModel> {

    public BooleanModelDeserializer() {
        this(null);
    }

    public BooleanModelDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public BooleanModel getNullValue(DeserializationContext ctxt) {
        return BooleanModel.NOT_SET;
    }


    @Override
    public BooleanModel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        Boolean val = node.asBoolean();
        return BooleanModel.fromBoolean(val);
    }
}
