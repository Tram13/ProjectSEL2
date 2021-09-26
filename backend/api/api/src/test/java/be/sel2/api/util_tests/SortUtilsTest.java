package be.sel2.api.util_tests;

import be.sel2.api.util.SortUtils;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortUtilsTest {

    @Test
    void defaultIsUnsorted() {
        Map<String, String> parameters = new HashMap<>();

        Sort sort = SortUtils.sortWithParameters(parameters);

        assertTrue(sort.isUnsorted());
    }

    @Test
    void defaultIsAscending() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("sortBy", "name");

        Sort sort = SortUtils.sortWithParameters(parameters);

        assertTrue(sort.isSorted());
        assertEquals(sort.ascending(), sort);
    }

    @Test
    void usesAscendingDirectionParameter() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("sortBy", "name");
        parameters.put("sortDirection", "asc");

        Sort sort = SortUtils.sortWithParameters(parameters);

        assertTrue(sort.isSorted());
        assertEquals(sort.ascending(), sort);
    }

    @Test
    void usesDescendingDirectionParameter() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("sortBy", "name");
        parameters.put("sortDirection", "desc");

        Sort sort = SortUtils.sortWithParameters(parameters);

        assertTrue(sort.isSorted());
        assertEquals(sort.descending(), sort);
    }

    /**
     * A largely unused class, containing a field with name "sortableValue" and nothing else
     * Used to test sortWithParameters(parameters, class)
     */
    private static class SortExample {
        public int sortableValue;
    }

    @Test
    void usedClassFields(){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("sortBy", "name");

        Sort sort = SortUtils.sortWithParameters(parameters, SortExample.class);

        assertTrue(sort.isUnsorted());

        parameters.put("sortBy", "sortableValue");

        sort = SortUtils.sortWithParameters(parameters, SortExample.class);

        assertTrue(sort.isSorted());
    }
}
