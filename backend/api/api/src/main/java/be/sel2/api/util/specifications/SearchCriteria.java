package be.sel2.api.util.specifications;

/**
 * Search criterium attempting to match a value in the database with key,
 * to the given value.
 */
public class SearchCriteria {

    private String key;
    private Object value;

    /**
     * Creates a new {@link SearchCriteria} to search the Repository
     *
     * @param key   The name of the field that needs to be searched
     * @param value The value we are searching
     */
    public SearchCriteria(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SearchCriteria{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }

}
