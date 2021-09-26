package be.sel2.api.datajpa_tests;

import be.sel2.api.entities.Organisation;
import be.sel2.api.entities.Proposal;
import be.sel2.api.repositories.OrganisationRepository;
import be.sel2.api.repositories.ProposalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class ProposalDataJpaTests {
    @Autowired
    private ProposalRepository repo;
    @Autowired
    private OrganisationRepository organisationRepo;

    private Date getDefaultDate() {
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdformat.parse("2030-05-18");
        } catch (ParseException e) {
            return null;
        }
    }

    private Organisation getDefaultOrganisation() {
        return new Organisation("Faculteit Wetenschappen",
                "1111111111",
                "OVO123456",
                "12345",
                "DienstServies");
    }

    private Proposal getDefaultProposal(boolean saveOrganisation) {
        Organisation org = getDefaultOrganisation();
        if (saveOrganisation) {
            organisationRepo.save(org);
        }
        Proposal proposal = new Proposal();
        proposal.setName("Meer ijsjes tijdens bestuursraad");
        proposal.setStatus(Proposal.ProposalStatus.IN_REVIEW);
        proposal.setDeadline(getDefaultDate());
        proposal.setLegalDeadline(getDefaultDate());
        proposal.setBusinessContext("Meer ijsjes -> Meer focus");
        proposal.setLegalContext("Ijsjes eten is legaal.");
        proposal.setFunctionalSetup("Afkoeling tijdens verhitte discussies");
        proposal.setTechnicalSetup("Ijsjes kopen in de winkel en dan opeten");
        proposal.setOrganisation(org);
        return proposal;
    }

    @Test
    void checkProposalEquals() {
        Proposal proposal1 = getDefaultProposal(true);
        repo.save(proposal1);
        Long proposal1Id = proposal1.getId();
        Organisation proposal1Organisation = proposal1.getOrganisation();

        Proposal proposal2 = getDefaultProposal(false);
        proposal2.setOrganisation(proposal1Organisation);
        repo.save(proposal2);
        assertNotEquals(proposal1, proposal2); // ID verschillend

        Proposal proposal3 = getDefaultProposal(false);
        proposal3.setOrganisation(proposal1Organisation);
        proposal3.setId(proposal1Id);
        assertEquals(proposal1.getId(), proposal3.getId());
        assertEquals(proposal1.getName(), proposal3.getName());
        assertEquals(proposal1.getStatus(), proposal3.getStatus());

        System.out.println(proposal1.getDeadline());
        System.out.println(proposal3.getDeadline());
        assertEquals(proposal1.getDeadline(), proposal3.getDeadline());
        assertEquals(proposal1.getLegalDeadline(), proposal3.getLegalDeadline());
        assertEquals(proposal1, proposal3); // Compleet gelijk

        Proposal proposal1Changed = getDefaultProposal(false);
        proposal1Changed.setOrganisation(proposal1Organisation);
        proposal1Changed.setId(proposal1Id);
        proposal1Changed.setName("Meer snoepjes");
        assertNotEquals(proposal1, proposal1Changed);

        proposal1Changed = getDefaultProposal(false);
        proposal1Changed.setOrganisation(proposal1Organisation);
        proposal1Changed.setId(proposal1Id);
        proposal1Changed.setStatus(Proposal.ProposalStatus.CANCELLED);
        assertNotEquals(proposal1, proposal1Changed);

        proposal1Changed = getDefaultProposal(false);
        proposal1Changed.setOrganisation(proposal1Organisation);
        proposal1Changed.setId(proposal1Id);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 3); // Ander tijdstip instellen
        proposal1Changed.setDeadline(calendar.getTime());
        assertNotEquals(proposal1, proposal1Changed);

        proposal1Changed = getDefaultProposal(false);
        proposal1Changed.setOrganisation(proposal1Organisation);
        proposal1Changed.setId(proposal1Id);
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 3); // Ander tijdstip instellen
        proposal1Changed.setLegalDeadline(calendar.getTime());
        assertNotEquals(proposal1, proposal1Changed);

        proposal1Changed = getDefaultProposal(false);
        proposal1Changed.setOrganisation(proposal1Organisation);
        proposal1Changed.setId(proposal1Id);
        proposal1Changed.setBusinessContext("Meer snoepjes -> meer focus");
        assertNotEquals(proposal1, proposal1Changed);

        proposal1Changed = getDefaultProposal(false);
        proposal1Changed.setOrganisation(proposal1Organisation);
        proposal1Changed.setId(proposal1Id);
        proposal1Changed.setLegalContext("Snoepjes eten is legaal.");
        assertNotEquals(proposal1, proposal1Changed);

        proposal1Changed = getDefaultProposal(false);
        proposal1Changed.setOrganisation(proposal1Organisation);
        proposal1Changed.setId(proposal1Id);
        proposal1Changed.setFunctionalSetup("Zoete snacks als tussendoortje");
        assertNotEquals(proposal1, proposal1Changed);

        proposal1Changed = getDefaultProposal(false);
        proposal1Changed.setOrganisation(proposal1Organisation);
        proposal1Changed.setId(proposal1Id);
        proposal1Changed.setTechnicalSetup("Naar de winkel gaan en snoepjes kopen");
        assertNotEquals(proposal1, proposal1Changed);

        proposal1Changed = getDefaultProposal(true); // Andere organisation
        proposal1Changed.setId(proposal1Id);
        assertNotEquals(proposal1, proposal1Changed);
    }

    @Test
    void checkSaveToRepository() {
        Proposal proposal = getDefaultProposal(true);
        assertNull(proposal.getId());
        repo.save(proposal);
        assertNotNull(proposal.getId());
        Proposal proposalCopy = repo.getOne(proposal.getId());
        assertNotNull(proposalCopy);
        assertEquals(proposal, proposalCopy);
    }

    @Test
    void checkSaveUpdateToRepositoryIsEqual() {
        Proposal proposal = getDefaultProposal(true);
        repo.save(proposal);
        proposal.setName("Meer snoepjes tijdens bestuursraad");
        repo.save(proposal);
        Proposal proposalCopy = repo.getOne(proposal.getId());
        assertNotEquals("Meer ijsjes tijdens bestuursraad", proposalCopy.getName());
        assertEquals("Meer snoepjes tijdens bestuursraad", proposalCopy.getName());
    }

    @Test
    void checkSaveUpdateNotEquals() {
        Proposal proposal1 = getDefaultProposal(true);
        Proposal proposal2 = getDefaultProposal(false);
        proposal2.setOrganisation(proposal1.getOrganisation()); // Proposal 1 en 2 zijn nu gelijk
        assertNull(proposal1.getId());
        assertNull(proposal2.getId());
        assertEquals(proposal1.getName(), proposal2.getName());
        repo.save(proposal1);
        repo.save(proposal2);
        assertNotEquals(proposal1, proposal2); // ID is anders
        assertEquals(proposal1.getName(), proposal2.getName());
        proposal1.setName("Meer snoepjes tijdens bestuursraad");
        assertNotEquals(proposal1.getName(), proposal2.getName());
    }

    @Test
    void checkDeleteFromProposalById() {
        Proposal proposal = getDefaultProposal(true);
        repo.save(proposal);
        Proposal proposalCopy = repo.getOne(proposal.getId());
        assertNotNull(proposalCopy);
        Long proposalId = proposal.getId();
        repo.deleteById(proposalId);
        assertThrows(JpaObjectRetrievalFailureException.class, () -> repo.getOne(proposalId));
    }

    @Test
    void checkDeleteFromRepositoryByObject() {
        Proposal proposal = getDefaultProposal(true);
        repo.save(proposal);
        Proposal proposalCopy = repo.getOne(proposal.getId());
        assertNotNull(proposalCopy);
        Long proposalId = proposal.getId();
        repo.delete(proposal);
        assertEquals("Meer ijsjes tijdens bestuursraad", proposalCopy.getName());
        assertThrows(JpaObjectRetrievalFailureException.class, () -> repo.getOne(proposalId));
    }
}
