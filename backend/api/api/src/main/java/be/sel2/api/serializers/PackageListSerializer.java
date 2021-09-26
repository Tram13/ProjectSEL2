package be.sel2.api.serializers;

import be.sel2.api.controllers.PackageController;
import be.sel2.api.entities.Package;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * Attempt at replacing package ID with package href
 */
public class PackageListSerializer extends StdSerializer<Iterable<Package>> {

    private static final long serialVersionUID = 1L;

    public PackageListSerializer() {
        this(null);
    }

    protected PackageListSerializer(Class<Iterable<Package>> t) {
        super(t);
    }

    @Override
    public void serialize(Iterable<Package> packages, JsonGenerator gen, SerializerProvider provider) throws IOException {

        Set<Object> result = new HashSet<>();

        packages.forEach(
                pack -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("packageId", pack.getId());
                    map.put("href",
                            linkTo(PackageController.class).toUri() + "/" + pack.getId()
                    );

                    result.add(map);
                }
        );


        provider.defaultSerializeValue(result, gen);
    }
}
