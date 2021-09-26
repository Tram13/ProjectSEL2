package be.sel2.api.util_tests;

import be.sel2.api.entities.UserInfo;
import be.sel2.api.util.AllFieldSearchUtil;
import be.sel2.api.util.specifications.AnyIsInListSpecification;
import be.sel2.api.util.specifications.DefaultSpecification;
import be.sel2.api.util.specifications.SearchCriteria;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SpecificationTests {

    //Default specifications
    @Test
    void predicateListsIsEmptyByDefault() {
        DefaultSpecification<UserInfo> spec = new DefaultSpecification<>();

        assertTrue(spec.getList().isEmpty());
    }

    @Test
    void simulatingDefaultSpecification() {
        DefaultSpecification<UserInfo> spec = new DefaultSpecification<>();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "Tester");
        parameters.put("description", "Testdescription");
        parameters.put("sorted", "name");
        parameters.put("sortDirection", "asc");

        spec.addWithKey(parameters, "name");
        spec.addWithKey(parameters, "description", "descr");
        spec.addWithKey(parameters, "email"); // Should not be added

        assertEquals(2, spec.getList().size(), "An incorrect amount of criteria were added");

        boolean containsName = false;
        boolean containsDescription = false;

        for (SearchCriteria criteria : spec.getList()) {
            assertNotEquals("sorted", criteria.getKey());
            assertNotEquals("sortDirection", criteria.getKey());
            assertNotEquals("email", criteria.getKey());
            if ("name".equals(criteria.getKey())) {
                assertEquals(parameters.get("name"), criteria.getValue());
                containsName = true;
            } else if ("descr".equals(criteria.getKey())) {
                assertEquals(parameters.get("description"), criteria.getValue());
                containsDescription = true;
            }
        }

        assertTrue(containsName, "CriteriaList does not contain name");
        assertTrue(containsDescription, "CriteriaList does not contain description");
    }


    //Any is in list specification
    @Test
    void predicateListsIsEmptyByDefault2() {
        AnyIsInListSpecification<UserInfo> spec = new AnyIsInListSpecification<>();

        assertTrue(spec.getList().isEmpty());
    }

    @Test
    void simulatingAnyIsInListSpecification() {
        AnyIsInListSpecification<UserInfo> spec = new AnyIsInListSpecification<>();

        List<String> testList = new ArrayList<>();
        testList.add("Tester");
        testList.add("OtherPerson");

        spec.add(new SearchCriteria("name", testList));

        assertEquals(1, spec.getList().size());
        assertEquals("name", spec.getList().get(0).getKey());
        assertEquals(testList, spec.getList().get(0).getValue());
    }

    @Test
    void generalSearchSpec() {
        Map<String, String> parameters = new HashMap<>();

        DefaultSpecification<UserInfo> spec = AllFieldSearchUtil.getSpecification(UserInfo.class, parameters);

        assertTrue(spec.getList().isEmpty());

        parameters.put("generalSearch", "tEst");

        spec = AllFieldSearchUtil.getSpecification(UserInfo.class, parameters);

        assertFalse(spec.getList().isEmpty());

        for (SearchCriteria criteria : spec.getList()) {
            assertEquals("tEst", criteria.getValue());
        }

        Set<String> fields = spec.getList().stream().map(SearchCriteria::getKey)
                .collect(Collectors.toSet());

        assertEquals(Set.of("firstName", "lastName", "email"), fields, "Got: " + fields);
    }
}
