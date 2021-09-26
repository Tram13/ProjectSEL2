package be.sel2.api.exceptions.not_found;

public class NotFoundException extends RuntimeException { // Custom error

    public NotFoundException(String type, Long id) {
        super(String.format("Could not find %s with ID %d", type, id));
        setStackTrace(null);
    }

    public NotFoundException(String type, String fieldName, String fieldValue) {
        super(String.format("Could not find %s with field %s equal to %s", type, fieldName, fieldValue));
        setStackTrace(null);
    }

    public NotFoundException(String type, Long id, Long otherId) {
        super(String.format("Could not find %s with ID %d and the queryID %d", type, id, otherId));
        setStackTrace(null);
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        super.setStackTrace(new StackTraceElement[]{getStackTrace()[0]});
    }
}
