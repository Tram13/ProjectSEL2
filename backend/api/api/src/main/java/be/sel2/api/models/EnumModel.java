package be.sel2.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Model used to return the list of enum options in `/config` following the format:
 * <pre>
 * {
 *     "type": "string",
 *     "enum": [...]
 * }
 * </pre>
 */
@Getter
public class EnumModel {

    private static final String DEFAULT_TYPE = "string";

    private final String type;
    @JsonProperty("enum")
    private final List<String> enumValues;

    public EnumModel(Enum<?>[] enumValues) {
        this(DEFAULT_TYPE, enumValues);
    }

    public EnumModel(String type, Enum<?>[] enumValues) {
        this.type = type;
        this.enumValues = Arrays.stream(enumValues)
                .map(e -> e.name().toLowerCase()).collect(Collectors.toList());
    }

    public EnumModel(String[] enumValues) {
        this(DEFAULT_TYPE, enumValues);
    }

    public EnumModel(String type, String[] enumValues) {
        this.type = type;
        this.enumValues = Arrays.asList(enumValues);
    }

    public EnumModel(List<String> enumValues) {
        this(DEFAULT_TYPE, enumValues);
    }

    public EnumModel(String type, List<String> enumValues) {
        this.type = type;
        this.enumValues = enumValues;
    }
}
