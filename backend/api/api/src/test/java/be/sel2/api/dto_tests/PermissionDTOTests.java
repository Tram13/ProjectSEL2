package be.sel2.api.dto_tests;

import be.sel2.api.dtos.PermissionDTO;
import be.sel2.api.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PermissionDTOTests {

    @Test
    void testNullableList() {
        PermissionDTO dto = new PermissionDTO();

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

        assertTrue(nullCheckedFields.containsAll(List.of("name", "description", "code", "organisationId")));
    }

    @Test
    void testProofLinkNullMessage() {
        PermissionDTO dto = new PermissionDTO();

        assertThrows(InvalidInputException.class, () -> dto.testValidity(true));

        try {
            dto.testValidity(true);
        } catch (InvalidInputException ex) {
            assertTrue(ex.containsMessages());
            assertTrue(ex.getErrors().stream()
                    .filter(pair -> pair.getMessage().equals("at least one must not be null"))
                    .anyMatch(pair -> pair.getParameter().equals("proof|link")));
        }
    }

    @Test
    void testValidationSuccess() {
        PermissionDTO dto = new PermissionDTO();

        assertDoesNotThrow(() -> dto.testValidity(false));
        assertThrows(InvalidInputException.class, () -> dto.testValidity(true));

        dto.setCode("RS245");
        dto.setDescription("feraifougajirof");
        dto.setLink("www.foo.com");
        dto.setProof(1L);
        dto.setOrganisationId(1L);
        dto.setName("TestName");

        assertDoesNotThrow(() -> dto.testValidity(false));
        assertDoesNotThrow(() -> dto.testValidity(true));
    }
}
