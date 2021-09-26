package be.sel2.api.util_tests;

import be.sel2.api.entities.archive.relations.PackageProposalDeletedId;
import be.sel2.api.entities.archive.relations.ProposalServiceDeletedId;
import be.sel2.api.entities.relations.ContactProposal;
import be.sel2.api.entities.relations.ContactProposalId;
import be.sel2.api.entities.relations.PackageServiceId;
import be.sel2.api.entities.relations.ProposalServiceId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EqualsTests {

    @Test
    void compareProposalServiceDeletedId() {
        Date date1 = new Date();
        ProposalServiceDeletedId id1 = new ProposalServiceDeletedId(1L, 1L, date1);
        ProposalServiceDeletedId id2 = new ProposalServiceDeletedId(1L, 1L, date1);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());

        id2.setProposal(2L);
        assertNotEquals(id1, id2);
        id2.setProposal(1L);
        id2.setService(2L);
        assertNotEquals(id1, id2);
        id2.setService(1L);

        id2.setDeletedOn(Date.from(Instant.EPOCH));
        assertNotEquals(id1, id2);

        assertNotEquals(null,id1);
    }

    @Test
    void compareProposalServiceId() {
        ProposalServiceId id1 = new ProposalServiceId(1L, 1L);
        ProposalServiceId id2 = new ProposalServiceId(1L, 1L);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());

        id2.setProposal(2L);
        assertNotEquals(id1, id2);
        id2.setProposal(1L);
        id2.setService(2L);
        assertNotEquals(id1, id2);

        assertNotEquals(null,id1);
    }

    @Test
    void comparePackageProposalDeletedId() {
        Date date1 = new Date();
        PackageProposalDeletedId id1 = new PackageProposalDeletedId();
        id1.setPack(1L);
        id1.setProposal(1L);
        id1.setDeletedOn(date1);
        PackageProposalDeletedId id2 = new PackageProposalDeletedId();
        id2.setPack(1L);
        id2.setProposal(1L);
        id2.setDeletedOn(date1);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());

        id2.setProposal(2L);
        assertNotEquals(id1, id2);
        id2.setProposal(1L);
        id2.setPack(2L);
        assertNotEquals(id1, id2);
        id2.setPack(1L);

        id2.setDeletedOn(Date.from(Instant.EPOCH));
        assertNotEquals(id1, id2);

        assertNotEquals(null,id1);
    }

    @Test
    void compareContactProposalId() {
        ContactProposalId id1 = new ContactProposalId();
        id1.setContact(1L);
        id1.setProposal(1L);
        id1.setRole(ContactProposal.Contactrole.BUSINESS);
        ContactProposalId id2 = new ContactProposalId();
        id2.setContact(1L);
        id2.setProposal(1L);
        id2.setRole(ContactProposal.Contactrole.BUSINESS);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());

        id2.setProposal(2L);
        assertNotEquals(id1, id2);
        id2.setProposal(1L);
        id2.setContact(2L);
        assertNotEquals(id1, id2);
        id2.setContact(1L);

        id2.setRole(ContactProposal.Contactrole.TECHNICAL);
        assertNotEquals(id1, id2);

        assertNotEquals(null,id1);
    }

    @Test
    void comparePackageServiceId() {
        PackageServiceId id1 = new PackageServiceId();
        id1.setService(1L);
        id1.setPack(1L);
        PackageServiceId id2 = new PackageServiceId();
        id2.setService(1L);
        id2.setPack(1L);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());

        id2.setService(2L);
        assertNotEquals(id1, id2);
        id2.setService(1L);
        id2.setPack(2L);
        assertNotEquals(id1, id2);
        id2.setPack(1L);

        assertNotEquals(null,id1);
    }
}
