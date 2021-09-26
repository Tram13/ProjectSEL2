package be.sel2.api.dto_tests;

import be.sel2.api.dtos.CertificateDTO;
import be.sel2.api.entities.Certificate;
import be.sel2.api.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CertificateDTOTests {

    @Test
    void testVerify() {
        CertificateDTO dto = new CertificateDTO();

        assertDoesNotThrow(() -> dto.testValidity(false));
        assertThrows(InvalidInputException.class, () -> dto.testValidity(true));

        try {
            dto.testValidity(true);
        } catch (InvalidInputException ex) {
            assertTrue(ex.containsMessages());
            assertTrue(ex.getErrors().stream().anyMatch(pair -> pair.getParameter().equals("file")));
            assertTrue(ex.getErrors().stream().anyMatch(pair -> pair.getParameter().equals("proposalId")));
        }

        dto.setFile(-5L);
        dto.setProposalId(1L);

        //No rules were implemented other than null checks
        assertDoesNotThrow(() -> dto.testValidity(false));
        assertDoesNotThrow(() -> dto.testValidity(true));
    }

    @Test
    void tryCreateEntity() {
        Random rand = new Random();
        CertificateDTO dto = new CertificateDTO();
        Long propId = rand.nextLong();
        Long fileId = rand.nextLong();

        dto.setProposalId(propId);
        dto.setFile(fileId);

        Certificate result = dto.getEntity();

        assertNotNull(result);
        assertNotNull(result.getFile());
        assertNotNull(result.getProposal());

        assertEquals(fileId, result.getFile().getId());
        assertEquals(propId, result.getProposal().getId());
    }

    @Test
    void updateShouldDoNothing() {
        Random rand = new Random();
        CertificateDTO dto = new CertificateDTO();
        Long propId = rand.nextLong();
        Long fileId = rand.nextLong();

        dto.setProposalId(propId);
        dto.setFile(fileId);

        Certificate cert = new Certificate();

        dto.updateEntity(cert); //PATCH does not exist, this should not alter anything

        assertEquals(new Certificate(), cert);
    }
}
