package be.sel2.api.util;

import be.sel2.api.util.specifications.AllFieldSpecification;
import be.sel2.api.util.specifications.DefaultSpecification;

import java.util.Map;

public class AllFieldSearchUtil {

    private static final String SEARCH_KEY = "generalSearch";

    private AllFieldSearchUtil() {
    }

    public static <T> DefaultSpecification<T> getSpecification(Class<?> c, Map<String, String> parameters) {
        if (parameters.containsKey(SEARCH_KEY)) {
            AllFieldSpecification<T> spec = new AllFieldSpecification<>();

            spec.addForClass(c, parameters.get(SEARCH_KEY));
            return spec;
        }
        return new DefaultSpecification<>();
    }
}
