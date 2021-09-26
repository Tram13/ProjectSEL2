package be.sel2.api.datajpa_tests;

import be.sel2.api.entities.Contact;
import be.sel2.api.entities.Organisation;
import be.sel2.api.entities.Proposal;
import be.sel2.api.entities.relations.ContactProposal;
import be.sel2.api.entities.relations.ContactProposalId;
import be.sel2.api.repositories.ContactProposalRepository;
import be.sel2.api.repositories.ContactRepository;
import be.sel2.api.repositories.OrganisationRepository;
import be.sel2.api.repositories.ProposalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // Set the active profile to use application-test.properties
@DataJpaTest
class ContactProposalDataJpaTests {
    @Autowired
    private ContactProposalRepository repo;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private OrganisationRepository organisationRepository;
    private Organisation org;

    @BeforeEach
    void setup() {
        org = getDefaultOrganisation();
        organisationRepository.save(org);
    }

    private Organisation getDefaultOrganisation() {
        return new Organisation("Faculteit Wetenschappen",
                "1111111111",
                "OVO123456",
                "12345",
                "DienstServies");
    }

    private Proposal getDefaultProposal1(Organisation org) {
        Proposal proposal = new Proposal();
        proposal.setName("Ijsjes tijdens bestuursraad");
        proposal.setStatus(Proposal.ProposalStatus.ACCEPTED);
        proposal.setFeedback("Goed idee!");
        proposal.setOrganisation(org);
        return proposal;
    }

    private Proposal getDefaultProposal2(Organisation org) {
        Proposal proposal = new Proposal();
        proposal.setName("Meer snoepjes tijdens bestuursraad");
        proposal.setStatus(Proposal.ProposalStatus.IN_REVIEW);
        proposal.setBusinessContext("Meer snoepjes -> Meer focus");
        proposal.setLegalContext("Ijsjes eten is legaal.");
        proposal.setFunctionalSetup("Afkoeling tijdens verhitte discussies");
        proposal.setTechnicalSetup("Ijsjes kopen in de winkel en dan opeten");
        proposal.setOrganisation(org);
        return proposal;
    }

    private Contact getDefaultContact1() {
        Contact contact = new Contact();
        contact.setFirstName("Jean-Claude");
        contact.setLastName("Van Damme");
        contact.setEmail("jcvd@gmail.com");
        contact.setPhoneNumber("+3292644770");
        contact.setOrganisation(org);
        return contact;
    }

    private Contact getDefaultContact2() {
        Contact contact = new Contact();
        contact.setFirstName("Piet");
        contact.setLastName("Piraat");
        contact.setEmail("PietPiraat@studio100.be");
        contact.setPhoneNumber("+3292644770");
        contact.setOrganisation(org);
        return contact;
    }

    @Test
    void checkContactProposalEquals() {
        Contact c1 = getDefaultContact1();
        Proposal p1 = getDefaultProposal1(org);
        contactRepository.save(c1);
        proposalRepository.save(p1);
        ContactProposal cp1 = new ContactProposal( // Geldig ContactProposal maken
                p1,
                c1,
                ContactProposal.Contactrole.SUBMITTER
        );
        repo.save(cp1);

        // Willekeurig gekozen andere klasse
        Contact randomObject = getDefaultContact1();
        assertNotEquals(cp1, randomObject);

        // Verschillend ContactProposal maken
        Contact c2 = getDefaultContact2();
        Proposal p2 = getDefaultProposal2(org);
        contactRepository.save(c2);
        proposalRepository.save(p2);
        ContactProposal cp2 = new ContactProposal(
                p2,
                c2,
                ContactProposal.Contactrole.TECHNICAL
        );
        repo.save(cp2);
        assertNotEquals(cp1, cp2);

        ContactProposal cp1Copy = new ContactProposal(
                p1,
                c1,
                ContactProposal.Contactrole.SUBMITTER
        );
        assertEquals(cp1, cp1Copy); // Zelfde parameters, dus moet gelijk zijn

        // Contact verschillend
        ContactProposal cp3 = new ContactProposal(
                p1,
                c2,
                ContactProposal.Contactrole.SUBMITTER
        );
        assertNotEquals(cp1, cp3);

        // Proposal verschillend
        ContactProposal cp4 = new ContactProposal(
                p2,
                c1,
                ContactProposal.Contactrole.SUBMITTER
        );
        assertNotEquals(cp1, cp4);

        // ContactRole verschillend
        ContactProposal cp5 = new ContactProposal(
                p1,
                c1,
                ContactProposal.Contactrole.BUSINESS
        );
        assertNotEquals(cp1, cp5);
    }

    @Test
    void checkSaveNewContactProposalToRepository() {
        Contact c = getDefaultContact1();
        contactRepository.save(c);
        Proposal p = getDefaultProposal1(org);
        proposalRepository.save(p);
        assertNull(p.getContacts());

        ContactProposal cp = new ContactProposal(
                p,
                c,
                ContactProposal.Contactrole.SUBMITTER
        );
        repo.save(cp);
        ContactProposal cpCopy = repo.getOne(cp.getId());
        assertNotNull(cpCopy);
        assertEquals(cp, cpCopy);
    }

    @Test
    void checkSaveUpdateToRepository() {
        Contact c = getDefaultContact1();
        contactRepository.save(c);
        Proposal p = getDefaultProposal1(org);
        proposalRepository.save(p);
        assertNull(p.getContacts());

        ContactProposal cp = new ContactProposal(
                p,
                c,
                ContactProposal.Contactrole.SUBMITTER
        );
        repo.save(cp);
        assertEquals(ContactProposal.Contactrole.SUBMITTER, cp.getRole());

        cp.setRole(ContactProposal.Contactrole.SAFETY_CONSULTANT);
        repo.save(cp);
        ContactProposal cpCopy = repo.getOne(cp.getId());
        assertNotEquals(ContactProposal.Contactrole.SUBMITTER, cpCopy.getRole());
        assertEquals(ContactProposal.Contactrole.SAFETY_CONSULTANT, cpCopy.getRole());
    }

    @Test
    void checkSaveUpdateNotEquals() {
        Contact c = getDefaultContact1();
        contactRepository.save(c);
        Proposal p = getDefaultProposal1(org);
        proposalRepository.save(p);
        assertNull(p.getContacts());

        ContactProposal cp1 = new ContactProposal(
                p,
                c,
                ContactProposal.Contactrole.SUBMITTER
        );
        ContactProposal cp2 = new ContactProposal(
                p,
                c,
                ContactProposal.Contactrole.SUBMITTER
        );
        repo.save(cp1);
        repo.saveAndFlush(cp2);
        assertEquals(cp1.getRole(), cp2.getRole());
        cp1.setRole(ContactProposal.Contactrole.BUSINESS);
        repo.saveAndFlush(cp1);
        assertNotEquals(cp1.getRole(), cp2.getRole());
    }

    @Test
    void checkDeleteFromRepositoryById() {
        Contact c = getDefaultContact1();
        contactRepository.save(c);
        Proposal p = getDefaultProposal1(org);
        proposalRepository.save(p);

        ContactProposal cp = new ContactProposal(
                p,
                c,
                ContactProposal.Contactrole.SUBMITTER
        );
        repo.save(cp);
        ContactProposal cpCopy = repo.getOne(cp.getId());
        assertNotNull(cpCopy);
        ContactProposalId id = cp.getId();
        repo.deleteById(id);
        assertThrows(JpaObjectRetrievalFailureException.class, () -> repo.getOne(id));
    }

    @Test
    void checkDeleteFromRepositoryByObject() {
        Contact c = getDefaultContact1();
        contactRepository.save(c);
        Proposal p = getDefaultProposal1(org);
        proposalRepository.save(p);
        assertNull(p.getContacts());

        ContactProposal cp = new ContactProposal(
                p,
                c,
                ContactProposal.Contactrole.SUBMITTER
        );
        repo.save(cp);
        ContactProposal cpCopy = repo.getOne(cp.getId());
        assertNotNull(cpCopy);
        ContactProposalId id = cp.getId();
        repo.delete(cp);
        assertThrows(JpaObjectRetrievalFailureException.class, () -> repo.getOne(id));
    }

    @Test
    void creatingContactProposalDoesCreateContactsSetInProposal() {
        Contact c1 = getDefaultContact1();
        contactRepository.save(c1);
        Proposal p1 = getDefaultProposal1(org);
        proposalRepository.save(p1);
        assertNull(p1.getContacts());

        ContactProposal cp1 = new ContactProposal(
                p1,
                c1,
                ContactProposal.Contactrole.SUBMITTER
        );
        repo.saveAndFlush(cp1);

        proposalRepository.refresh(p1);

        assertNotNull(p1.getContacts());
        assertNotEquals(0, p1.getContacts().size());
        assertEquals(1, p1.getContacts().size());
        assertTrue(p1.getContacts().stream().findFirst().isPresent());
        assertEquals(c1, p1.getContacts().stream().findFirst().get().getContact());
    }

    @Test
    void creatingMultipleContactProposalDoesCreateContactsSetInProposal() {
        Contact c1 = getDefaultContact1();
        contactRepository.save(c1);
        Contact c2 = getDefaultContact2();
        contactRepository.save(c2);
        Proposal p1 = getDefaultProposal1(org);
        proposalRepository.save(p1);
        assertNull(p1.getContacts());

        ContactProposal cp1 = new ContactProposal(
                p1,
                c1,
                ContactProposal.Contactrole.SUBMITTER
        );
        repo.save(cp1);

        ContactProposal cp2 = new ContactProposal(
                p1,
                c2,
                ContactProposal.Contactrole.TECHNICAL
        );
        repo.saveAndFlush(cp2);

        proposalRepository.refresh(p1);

        assertNotNull(p1.getContacts());
        assertNotEquals(0, p1.getContacts().size());
        assertEquals(2, p1.getContacts().size());
        Set<Contact> cpSet = p1.getContacts().stream().map(ContactProposal::getContact).collect(Collectors.toSet());
        Set<Contact> cpSetOriginal = Set.of(c1, c2);
        assertEquals(cpSet, cpSetOriginal);
    }

    @Test
    void deleteContactProposalAlsoDeletesFromSet() {
        Contact c1 = getDefaultContact1();
        contactRepository.save(c1);
        Contact c2 = getDefaultContact2();
        contactRepository.save(c2);
        Proposal p1 = getDefaultProposal1(org);
        proposalRepository.save(p1);
        assertNull(p1.getContacts());

        ContactProposal cp1 = new ContactProposal(
                p1,
                c1,
                ContactProposal.Contactrole.SUBMITTER
        );
        repo.save(cp1);

        ContactProposal cp2 = new ContactProposal(
                p1,
                c2,
                ContactProposal.Contactrole.TECHNICAL
        );
        repo.saveAndFlush(cp2);

        proposalRepository.refresh(p1);
        assertEquals(2, p1.getContacts().size());

        repo.delete(cp1);
        repo.flush();
        proposalRepository.refresh(p1);
        assertEquals(1, p1.getContacts().size());
        assertTrue(p1.getContacts().stream().findFirst().isPresent());
        ContactProposal cp2Copy = p1.getContacts().stream().findFirst().get();
        assertEquals(cp2, cp2Copy);
    }

    @Test
    void updateContactProposalAlsuUpdatesPackage() {
        Contact c1 = getDefaultContact1();
        contactRepository.save(c1);
        Contact c1Original = getDefaultContact1();
        assertEquals(c1.getEmail(), c1Original.getEmail());
        Proposal p1 = getDefaultProposal1(org);
        proposalRepository.save(p1);
        assertNull(p1.getContacts());

        ContactProposal cp1 = new ContactProposal(
                p1,
                c1,
                ContactProposal.Contactrole.SUBMITTER
        );
        repo.saveAndFlush(cp1);

        proposalRepository.refresh(p1);
        assertEquals(1, p1.getContacts().size());
        assertTrue(p1.getContacts().stream().findFirst().isPresent());
        ContactProposal cp1Copy = p1.getContacts().stream().findFirst().get();
        assertEquals(c1, cp1Copy.getContact());

        // Onrechtstreeks aanpassen van ContactProposal
        c1.setEmail("gunnar.brinkmann@ugent.be");
        contactRepository.saveAndFlush(c1);
        proposalRepository.refresh(p1);

        // Controle of de aanpassing propageert naar ContactProposal
        assertTrue(p1.getContacts().stream().findFirst().isPresent());
        cp1Copy = p1.getContacts().stream().findFirst().get();
        assertNotEquals(c1Original.getEmail(), cp1Copy.getContact().getEmail()); // Oude waarde
        assertEquals(c1.getEmail(), cp1Copy.getContact().getEmail());
    }
}
