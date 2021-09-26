package be.sel2.api.dtos;

import be.sel2.api.exceptions.InvalidInputException;

import java.util.List;
import java.util.function.Predicate;

/**
 * Class used to read input from user before converting it to an entity
 */
public abstract class DTOObject<T> {

    protected String nullMessage = "must not be null";

    /**
     * Defines the list of non nullable fields in the DTO
     */
    protected abstract List<String> getInvalidNonNullableFields();

    /**
     * Tests the validity of all DTO fields, if any invalids were found,
     * an {@link InvalidInputException} is thrown.
     */
    public abstract void testValidity(boolean requireNotNullFields) throws InvalidInputException;


    /**
     * Builds a database Entity based on this DTO
     */
    public abstract T getEntity();

    /**
     * Updates an existing database Entity based on this DTO
     */
    public abstract void updateEntity(T other);

    /**
     * Validates an input field using validator,
     * if false, a message is added to the exception
     */
    protected void validateField(String value, String name,
                                 Predicate<String> validator, String message,
                                 InvalidInputException ex) {
        if (value != null && !validator.test(value)) {
            ex.addMessage(name, message);
        }
    }

    /**
     * Tests all currently unchecked fields for max length
     * and adds them to {@link InvalidInputException} ex.
     * Is empty by default.
     */
    protected void validateMaxInputLength(InvalidInputException ex) {
    }

    /**
     * Builds a {@link InvalidInputException} based on the list of non nullable fields
     * and max input length.
     */
    protected InvalidInputException buildException(boolean requireNotNullFields) {
        InvalidInputException exceptionList = new InvalidInputException();
        if (requireNotNullFields) {

            for (String parameter : getInvalidNonNullableFields()) {
                exceptionList.addMessage(parameter, nullMessage);
            }
        }
        validateMaxInputLength(exceptionList);
        return exceptionList;
    }
}
