package be.sel2.api.dto_tests;

import be.sel2.api.dtos.OrganisationDTO;
import be.sel2.api.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class OrganisationDTOTests {

    @Test
    void testNullableList() {
        OrganisationDTO dto = new OrganisationDTO();

        assertThrows(InvalidInputException.class, () -> dto.testValidity(true));

        List<String> nullCheckedFields = List.of();

        try {
            dto.testValidity(true);
        } catch (InvalidInputException ex) {
            assertTrue(ex.containsMessages());
            nullCheckedFields = ex.getErrors().stream()
                    .filter(pair -> pair.getMessage().equals("must not be null"))
                    .map(InvalidInputException.ParamErrorPair::getParameter)
                    .collect(Collectors.toList());
        }

        assertTrue(nullCheckedFields.containsAll(
                List.of("organisationName", "kboNumber", "serviceProvider")));

        dto.setOrganisationName("testOrganisation");
        dto.setKboNumber("0575748966");
        dto.setServiceProvider("TestProvider");

        assertDoesNotThrow(() -> dto.testValidity(true));
    }
}
