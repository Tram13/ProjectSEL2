package be.sel2.api.util;

import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utils to easily create {@link Sort} objects based on the user input parameters
 */
public class SortUtils {

    private SortUtils() {
    } // Prevent instantiation

    public static final List<String> DATE_FIELDS = List.of("created", "lastUpdated");

    public static Sort sortWithParameters(Map<String, String> parameters, Class<?> c) {
        return sortWithParameters(parameters,
                Arrays.stream(c.getDeclaredFields()).map(Field::getName)
                        .collect(Collectors.toList())
        );
    }

    public static Sort sortWithParameters(Map<String, String> parameters) {
        return sortWithParameters(parameters, (List<String>) null);
    }

    public static Sort sortWithParameters(Map<String, String> parameters, List<String> permittedParameters) {
        String sortedKey;
        if (parameters.containsKey("sortBy")) {
            sortedKey = parameters.get("sortBy");
            if (permittedParameters != null && !permittedParameters.contains(sortedKey)) {
                return Sort.unsorted();
            }
        } else {
            return Sort.unsorted();
        }

        Sort.Direction direction = Sort.DEFAULT_DIRECTION;
        if (parameters.containsKey("sortDirection") && "desc".equals(parameters.get("sortDirection"))) {
            direction = Sort.Direction.DESC;
        }

        return Sort.by(direction, sortedKey);
    }
}
