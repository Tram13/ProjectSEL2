package be.sel2.api.util;

import be.sel2.api.controllers.ServeController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Utils class that generates the path a static file is being served on based on it's filename
 */
public class StaticFilePathBuilder {

    private StaticFilePathBuilder() {
        throw new IllegalStateException("Utility class");
    }

    public static String buildServePath(String fileName) {
        return linkTo(methodOn(ServeController.class).serveFile(fileName)).toString();
    }
}
