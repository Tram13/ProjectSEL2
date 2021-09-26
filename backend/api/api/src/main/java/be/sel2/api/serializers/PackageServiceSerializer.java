package be.sel2.api.serializers;

import be.sel2.api.controllers.ServiceController;
import be.sel2.api.entities.relations.PackageService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * Attempt at replacing service ID with service href
 */
public class PackageServiceSerializer extends StdSerializer<Set<PackageService>> {

    private static final long serialVersionUID = 1L;

    public PackageServiceSerializer() {
        this(null);
    }

    protected PackageServiceSerializer(Class<Set<PackageService>> t) {
        super(t);
    }

    private String getBaseUrl() {
        String baseUrl = linkTo(ServiceController.class).toString();
        if (!baseUrl.endsWith("/")) {
            baseUrl += '/';
        }
        return baseUrl;
    }

    @Override
    public void serialize(Set<PackageService> serviceSet, JsonGenerator gen, SerializerProvider provider) throws IOException {
        String baseUrl = getBaseUrl();
        Set<Map<String, String>> services = serviceSet.stream().map(s ->
                Map.of(
                        "source", s.getSource(),
                        "deliveryMethod", s.getDeliveryMethod().toString(),
                        "href", baseUrl + s.getService().getId()
                )
        ).collect(Collectors.toSet());

        provider.defaultSerializeValue(services, gen);
    }
}
