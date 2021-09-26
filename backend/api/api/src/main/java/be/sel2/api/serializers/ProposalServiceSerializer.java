package be.sel2.api.serializers;

import be.sel2.api.controllers.ServiceController;
import be.sel2.api.entities.relations.ProposalService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * Attempt at replacing service ID with service href
 */
public class ProposalServiceSerializer extends StdSerializer<ProposalService> {

    private static final long serialVersionUID = 1L;

    public ProposalServiceSerializer() {
        this(null);
    }

    protected ProposalServiceSerializer(Class<ProposalService> t) {
        super(t);
    }

    @Override
    public void serialize(ProposalService propServ, JsonGenerator gen, SerializerProvider provider) throws IOException {

        Map<String, Object> map = new HashMap<>();
        map.put("serviceId", propServ.getService().getId());
        map.put("href",
                linkTo(ServiceController.class).toUri() + "/" + propServ.getService().getId()
        );
        map.put("source", propServ.getSource());
        map.put("deliveryMethod", propServ.getDeliveryMethod());


        provider.defaultSerializeValue(map, gen);
    }
}
