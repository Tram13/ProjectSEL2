package be.sel2.api.serializers;

import be.sel2.api.controllers.ContactController;
import be.sel2.api.entities.relations.ContactProposal;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * Attempt at replacing contact ID with contact href
 */
public class ContactProposalSerializer extends StdSerializer<ContactProposal> {

    private static final long serialVersionUID = 1L;

    public ContactProposalSerializer() {
        this(null);
    }

    protected ContactProposalSerializer(Class<ContactProposal> t) {
        super(t);
    }

    @Override
    public void serialize(ContactProposal propCon, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("contactId", propCon.getContact().getId());
        map.put("href",
                linkTo(ContactController.class).toUri() + "/" + propCon.getContact().getOrganisation().getId()
                        + "/contacts/" + propCon.getContact().getId());
        map.put("role", propCon.getRole());

        provider.defaultSerializeValue(map, gen);

    }


}
