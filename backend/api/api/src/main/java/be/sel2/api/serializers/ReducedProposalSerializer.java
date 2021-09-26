package be.sel2.api.serializers;

import be.sel2.api.entities.Proposal;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReducedProposalSerializer extends StdSerializer<Proposal> {

    public ReducedProposalSerializer() {
        this(null);
    }

    public ReducedProposalSerializer(Class<Proposal> t) {
        super(t);
    }

    @Override
    public void serialize(Proposal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Map<String, Object> body = new HashMap<>();

        body.put("id", value.getId());
        body.put("name", value.getName());
        body.put("status", value.getStatus());
        body.put("organisation", value.getOrganisation());

        provider.defaultSerializeValue(body, gen);
    }
}
