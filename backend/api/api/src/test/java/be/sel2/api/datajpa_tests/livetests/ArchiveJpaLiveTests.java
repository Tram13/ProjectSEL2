package be.sel2.api.datajpa_tests.livetests;

import be.sel2.api.entities.Organisation;
import be.sel2.api.entities.archive.OrganisationDeleted;
import be.sel2.api.repositories.OrganisationRepository;
import be.sel2.api.repositories.archive.ArchivedOrganisationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
// ** IMPORTANT NOTICE **
// These tests use an actual database, not a mocked one. This means that the database must be installed on the system
// that this test is executed on.
@ActiveProfiles("testwithdatabaseconnection")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIfEnvironmentVariable(named = "LIVETESTS", matches = "1")
class ArchiveJpaLiveTests {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ArchivedOrganisationRepository archivedRepo;

    @Test
    void checkRemovingOrganisation() {

        Organisation monarchy = new Organisation("Koningshuis België", "9999999990", "OVO123456", "55555", "DienstServies");
        monarchy = organisationRepository.saveAndFlush(monarchy);

        monarchy = organisationRepository.getOne(monarchy.getId());

        assertEquals("Koningshuis België", monarchy.getOrganisationName());

        organisationRepository.delete(monarchy);
        organisationRepository.flush();

        OrganisationDeleted deleted = archivedRepo.getOne(monarchy.getId());

        assertEquals(deleted.getCreated().getTime(), monarchy.getCreated().getTime());
    }
}
