package be.sel2.api.datajpa_tests;

import be.sel2.api.entities.Organisation;
import be.sel2.api.entities.Proposal;
import be.sel2.api.entities.Service;
import be.sel2.api.entities.relations.ProposalService;
import be.sel2.api.entities.relations.ProposalServiceId;
import be.sel2.api.repositories.OrganisationRepository;
import be.sel2.api.repositories.ProposalRepository;
import be.sel2.api.repositories.ProposalServiceRepository;
import be.sel2.api.repositories.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // Set the active profile to use application-test.properties
@DataJpaTest
class ProposalServiceDataJpaTests {

    @Autowired
    private ProposalServiceRepository repo;
    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private OrganisationRepository organisationRepository;

    private Service getDefaultService() {
        Service service = new Service(
                "GeefAantalAanwezigheden",
                "Onderwijs",
                "Het opvragen van aantal halve dagen aanwezigheid van een persoon op basis van diens INSZ en het schooljaar.",
                false,
                new HashSet<>(Arrays.asList("AgODi", "VTC")),
                new HashSet<>(Arrays.asList(Service.DeliveryMethod.FTP, Service.DeliveryMethod.WEBSERVICE))
        );
        serviceRepository.save(service);
        return service;
    }

    private Date getDefaultDate() {
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdformat.parse("2030-05-18");
        } catch (ParseException e) {
            return null;
        }
    }

    private Organisation getDefaultOrganisation() {
        Organisation organisation = new Organisation("Faculteit Wetenschappen",
                "1111111111",
                "OVO123456",
                "12345",
                "DienstServies");
        organisationRepository.save(organisation);
        return organisation;
    }

    private Proposal getDefaultProposal() {
        Organisation org = getDefaultOrganisation();
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
        proposalRepository.save(proposal);
        return proposal;
    }

    private ProposalService getDefaultProposalService() {
        return new ProposalService(
                getDefaultProposal(),
                getDefaultService(),
                Service.DeliveryMethod.FTP,
                "Fod Fin"
        );
    }

    @Test
    void checkProposalServiceEquals() {
        ProposalService proposalService = getDefaultProposalService();
        repo.save(proposalService);

        ProposalService proposalServiceChanged = getDefaultProposalService();
        proposalServiceChanged.setSource("Nieuwe bron");
        repo.save(proposalServiceChanged);
        assertNotEquals(proposalService, proposalServiceChanged);

        ProposalService proposalServiceEqual = getDefaultProposalService();
        proposalServiceEqual.setProposal(proposalService.getProposal());
        proposalServiceEqual.setService(proposalService.getService());
        repo.save(proposalServiceEqual);
        assertEquals(proposalService, proposalServiceEqual);

        proposalServiceChanged = getDefaultProposalService();
        proposalServiceEqual.setService(proposalService.getService());
        repo.save(proposalServiceChanged);
        assertNotEquals(proposalService, proposalServiceChanged); // Different Proposal

        proposalServiceChanged = getDefaultProposalService();
        proposalServiceEqual.setProposal(proposalService.getProposal());
        repo.save(proposalServiceChanged);
        assertNotEquals(proposalService, proposalServiceChanged); // Different Service

        proposalServiceChanged = getDefaultProposalService();
        proposalServiceEqual.setProposal(proposalService.getProposal());
        proposalServiceEqual.setService(proposalService.getService());
        proposalServiceChanged.setDeliveryMethod(Service.DeliveryMethod.MO);
        repo.save(proposalServiceChanged);
        assertNotEquals(proposalService, proposalServiceChanged); // Different DeliveryMethod

        proposalServiceChanged = getDefaultProposalService();
        proposalServiceEqual.setProposal(proposalService.getProposal());
        proposalServiceEqual.setService(proposalService.getService());
        proposalServiceChanged.setSource("New Source");
        repo.save(proposalServiceChanged);
        assertNotEquals(proposalService, proposalServiceChanged); // Different Source
    }

    @Test
    void checkSaveToRepository() {
        ProposalService proposalService = getDefaultProposalService();
        repo.save(proposalService);
        ProposalService proposalServiceCopy = repo.getOne(proposalService.getId());
        assertNotNull(proposalServiceCopy);
        assertEquals(proposalService, proposalServiceCopy);
    }

    @Test
    void checkSaveUpdateToRepositoryIsEqual() {
        ProposalService proposalService = getDefaultProposalService();
        repo.save(proposalService);
        proposalService.setSource("Nieuwe bron");
        repo.save(proposalService);
        ProposalService proposalServiceFromDatabase = repo.getOne(proposalService.getId());
        assertNotEquals("Fod Fin", proposalServiceFromDatabase.getSource());
        assertEquals("Nieuwe bron", proposalServiceFromDatabase.getSource());
    }

    @Test
    void checkSaveUpdateNotEquals() { // De waarde in entiteit 1 aanpassen verandert niets in entiteit 2
        ProposalService proposalService = getDefaultProposalService();
        ProposalService proposalServiceCopy = getDefaultProposalService();
        assertNotEquals(proposalService, proposalServiceCopy); // IDs verschillen
        assertEquals(proposalService.getProposal().getName(), proposalServiceCopy.getProposal().getName()); // De rest is wel gelijk
        assertEquals(proposalService.getService().getName(), proposalServiceCopy.getService().getName());
        assertEquals(proposalService.getDeliveryMethod(), proposalServiceCopy.getDeliveryMethod());
        proposalService.setDeliveryMethod(Service.DeliveryMethod.PUB);
        assertEquals(proposalService.getProposal().getName(), proposalServiceCopy.getProposal().getName()); // Dit is niet aangepast
        assertNotEquals(proposalService.getDeliveryMethod(), proposalServiceCopy.getDeliveryMethod());
    }

    @Test
    void checkDeleteFromRepoById() {
        ProposalService proposalService = getDefaultProposalService();
        repo.save(proposalService);
        ProposalService proposalServiceCopy = repo.getOne(proposalService.getId());
        assertNotNull(proposalServiceCopy);
        ProposalServiceId id = proposalService.getId();
        Proposal proposal = proposalService.getProposal();
        Service service = proposalService.getService();
        repo.deleteById(proposalService.getId());
        assertThrows(JpaObjectRetrievalFailureException.class, () -> repo.getOne(id));
        assertNotNull(proposalRepository.getOne(proposal.getId())); // Proposal moet nog steeds bestaan
        assertEquals("Meer ijsjes tijdens bestuursraad", proposal.getName());
        assertNotNull(serviceRepository.getOne(service.getId())); // Service moet nog steeds bestaan
        assertEquals("GeefAantalAanwezigheden", service.getName());
    }

    @Test
    void checkDeleteFromRepoByObject() {
        ProposalService proposalService = getDefaultProposalService();
        repo.save(proposalService);
        ProposalService proposalServiceCopy = repo.getOne(proposalService.getId());
        assertNotNull(proposalServiceCopy);
        ProposalServiceId id = proposalService.getId();
        Proposal proposal = proposalService.getProposal();
        Service service = proposalService.getService();
        repo.delete(proposalService);
        assertThrows(JpaObjectRetrievalFailureException.class, () -> repo.getOne(id));
        assertNotNull(proposalRepository.getOne(proposal.getId())); // Proposal moet nog steeds bestaan
        assertEquals("Meer ijsjes tijdens bestuursraad", proposal.getName());
        assertNotNull(serviceRepository.getOne(service.getId())); // Service moet nog steeds bestaan
        assertEquals("GeefAantalAanwezigheden", service.getName());
    }
}
