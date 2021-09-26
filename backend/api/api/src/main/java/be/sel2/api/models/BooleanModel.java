package be.sel2.api.models;

import be.sel2.api.serializers.BooleanModelDeserializer;
import be.sel2.api.serializers.BooleanModelSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = BooleanModelSerializer.class)
@JsonDeserialize(using = BooleanModelDeserializer.class)
public enum BooleanModel {
    TRUE(true),
    FALSE(false),
    NOT_SET(null);

    private final Boolean value;

    BooleanModel(Boolean value) {
        this.value = value;
    }

    public static BooleanModel fromBoolean(Boolean value) {
        if (value == null) {
            return NOT_SET;
        }
        if (value) {
            return TRUE;
        }
        return FALSE;
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value == null) {
            return "null";
        }
        return value.toString();
    }
}
