package be.sel2.api.datajpa_tests.livetests;

import be.sel2.api.entities.Organisation;
import be.sel2.api.repositories.OrganisationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
// ** IMPORTANT NOTICE **
// These tests use an actual database, not a mocked one. This means that the database must be installed on the system
// that this test is executed on.
@ActiveProfiles("testwithdatabaseconnection")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIfEnvironmentVariable(named = "LIVETESTS", matches = "1")
class OrganisationDataJpaLiveTests {

    @Autowired
    private OrganisationRepository organisationRepository;

    // Deze test kijkt of de timestamps effectief correct updaten in de databank na een update.
    // Dat gebeurt normaal automatisch door een trigger.
    @Test
    void checkIfLastUpdatedChangesCorrectly() throws InterruptedException {
        String extraMessage = "Is de databank up-to-date? Contacteer DBA.";
        Organisation monarchy = new Organisation("Koningshuis België", "1111111111", "OVO1234", "12345", "DienstServies");
        organisationRepository.saveAndFlush(monarchy);
        organisationRepository.refresh(monarchy);
        assertNotNull(monarchy.getLastUpdated());
        Date creationTime = monarchy.getLastUpdated();
        assertEquals(creationTime, monarchy.getLastUpdated());

        assertEquals(monarchy.getLastUpdated(), creationTime); // Tijd blijft gelijk
        TimeUnit.SECONDS.sleep(1); // Forceer tijdverschil
        monarchy.setNisNumber("15948");
        organisationRepository.saveAndFlush(monarchy); // Aanpassing opslaan

        organisationRepository.refresh(monarchy);
        assertNotEquals(monarchy.getLastUpdated(), creationTime, extraMessage);
    }
}
