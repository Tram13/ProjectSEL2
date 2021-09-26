package be.sel2.api.dto_tests;

import be.sel2.api.dtos.ProposalDTO;
import be.sel2.api.entities.Proposal;
import be.sel2.api.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProposalDTOTests {

    @Test
    void testValidateNullable() {
        ProposalDTO dto = new ProposalDTO();

        assertThrows(InvalidInputException.class, () -> dto.testValidity(true));
        assertDoesNotThrow(() -> dto.testValidity(false));

        dto.setName("TestName");
        dto.setOrganisationId(1L);

        assertDoesNotThrow(() -> dto.testValidity(true));
        assertDoesNotThrow(() -> dto.testValidity(false));
    }

    @Test
    void testValidateOptions() {
        ProposalDTO dto = new ProposalDTO();

        dto.setOptions(Set.of("BRIEF", "EBOX_BURGER", "EBOX_ONDERNEMER", "Quezalquat'l"));
        assertThrows(InvalidInputException.class, () -> dto.testValidity(false));

        dto.setOptions(Set.of("BRIEF", "EBOX_BURGER", "EBOX_ONDERNEMER"));
        assertDoesNotThrow(() -> dto.testValidity(false));

        dto.setOnlineOption("Scribbles");
        assertThrows(InvalidInputException.class, () -> dto.testValidity(false));

        dto.setOnlineOption("MAGDA_ONLINE_PRO");
        assertDoesNotThrow(() -> dto.testValidity(false));

        dto.setOnlineOption("WEB_SERVICE");
        assertDoesNotThrow(() -> dto.testValidity(false));

        dto.setEstimatedNumberOfRequests("+10");
        assertThrows(InvalidInputException.class, () -> dto.testValidity(false));

        dto.setEstimatedNumberOfRequests(Proposal.NumberOfRequests.LOW.toString());
        assertDoesNotThrow(() -> dto.testValidity(false));

        dto.setEstimatedNumberOfRequests(Proposal.NumberOfRequests.MID.toString());
        assertDoesNotThrow(() -> dto.testValidity(false));

        dto.setEstimatedNumberOfRequests(Proposal.NumberOfRequests.HIGH.toString());
        assertDoesNotThrow(() -> dto.testValidity(false));

        dto.setEstimatedNumberOfRequests(Proposal.NumberOfRequests.VERY_HIGH.toString());
        assertDoesNotThrow(() -> dto.testValidity(false));

        Proposal proposal = dto.getEntity();

        assertEquals(Proposal.OnlineOption.WEB_SERVICE, proposal.getOnlineOption());
        assertEquals(Set.of(
                Proposal.EboxOption.BRIEF,
                Proposal.EboxOption.EBOX_BURGER,
                Proposal.EboxOption.EBOX_ONDERNEMER),
                proposal.getOptions());
        assertEquals(Proposal.NumberOfRequests.VERY_HIGH, proposal.getEstimatedNumberOfRequests());
    }

    @Test
    void testNotNullableFieldsInRelations() {
        ProposalDTO dto = new ProposalDTO();

        dto.setContacts(Set.of(new ProposalDTO.ContactItem()));
        assertThrows(InvalidInputException.class, () -> dto.testValidity(false));
        dto.setContacts(Set.of());

        assertDoesNotThrow(() -> dto.testValidity(false));

        dto.setServices(Set.of(new ProposalDTO.ServiceItem()));
        assertThrows(InvalidInputException.class, () -> dto.testValidity(false));
        dto.setServices(Set.of());

        assertDoesNotThrow(() -> dto.testValidity(false));

        dto.setPackages(Set.of(new ProposalDTO.PackageItem()));
        assertThrows(InvalidInputException.class, () -> dto.testValidity(false));
    }

    @Test
    void testServiceItem() {
        ProposalDTO.ServiceItem serviceItem = new ProposalDTO.ServiceItem();

        serviceItem.setServiceId(1L);
        String invalidSource = "A very long and very invalid Source that far exceeds the maximum character limit which is also known as 256 characters to the point where I needed to add this string twice.";
        serviceItem.setSource(invalidSource.concat(invalidSource));
        serviceItem.setDeliveryMethod("invalidDeliveryMethod");

        InvalidInputException errorList = new InvalidInputException();

        serviceItem.validateMaxInputLength(errorList);

        assertTrue(errorList.containsMessages());

        assertEquals(2, errorList.getErrors().size());

        assertTrue(errorList.getErrors().stream().anyMatch(
                e -> e.getMessage().equals("may be at most 256 characters long") &&
                        e.getParameter().equals("services.source")
        ));

        assertTrue(errorList.getErrors().stream().anyMatch(
                e -> e.getMessage().matches("^Should be from options: \\[(((FTP)|(MO)|(PUB)|(WEBSERVICE))(, )?){4}]$") &&
                        e.getParameter().equals("services.deliveryMethod")
        ));
    }

    @Test
    void testContactItem() {
        ProposalDTO.ContactItem contactItem = new ProposalDTO.ContactItem();

        contactItem.setContactId(1L);
        contactItem.setRole("someValue");

        InvalidInputException errorList = new InvalidInputException();

        contactItem.validateValues(errorList);

        assertTrue(errorList.containsMessages());

        assertEquals(1, errorList.getErrors().size());

        InvalidInputException.ParamErrorPair pair = errorList.getErrors().get(0);

        assertEquals("contacts.role", pair.getParameter());
        assertTrue(pair.getMessage().matches("^Should be from options: \\[(((submitter)|(business)|" +
                "(business_backup)|(technical)|(technical_backup)|(safety_consultant)|" +
                "(service_provider)|(responsible_d2d_management_customer)|(manager_geosecure))(, )?){9}]$"));
    }
}
