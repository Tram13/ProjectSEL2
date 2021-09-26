package be.sel2.api.serializers;

import be.sel2.api.controllers.FileController;
import be.sel2.api.entities.FileMeta;
import be.sel2.api.util.StaticFilePathBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Serializer that converts FileMeta to href
 */
public class FileToHrefSerializer extends StdSerializer<FileMeta> {

    public FileToHrefSerializer() {
        this(null);
    }

    protected FileToHrefSerializer(Class<FileMeta> t) {
        super(t);
    }

    @Override
    public void serialize(FileMeta value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue(Map
                .of("href",
                        linkTo(methodOn(FileController.class)
                                .getFileById(value.getId())).toUri(),
                        "fileLocation",
                        StaticFilePathBuilder.buildServePath(value.getFileLocation())), gen);
    }
}
