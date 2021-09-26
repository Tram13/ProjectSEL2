package be.sel2.api.datajpa_tests.livetests;

import be.sel2.api.entities.UserInfo;
import be.sel2.api.repositories.UserRepository;
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
class UserDataJpaLiveTests {

    @Autowired
    private UserRepository userRepository;

    // Deze test kijkt of de timestamps effectief correct updaten in de databank na een update.
    // Dat gebeurt normaal automatisch door een trigger.
    @Test
    void checkIfLastUpdatedChangesCorrectly() throws InterruptedException {
        String extraMessage = "Is de databank up-to-date? Contacteer DBA.";
        UserInfo philippe = new UserInfo("Philippe", "de Belgique", "fluppe@monarchie.be", "MathildeIsBae", UserInfo.Userrole.ADMIN);
        userRepository.saveAndFlush(philippe);
        userRepository.refresh(philippe);
        assertNotNull(philippe.getLastUpdated());
        Date creationTime = philippe.getLastUpdated();
        assertEquals(creationTime, philippe.getLastUpdated());

        assertEquals(philippe.getLastUpdated(), creationTime); // Tijd blijft gelijk
        TimeUnit.SECONDS.sleep(1); // Forceer tijdverschil
        philippe.setEmail("phillytheking@monarchie.be");
        userRepository.saveAndFlush(philippe); // Aanpassing opslaan

        userRepository.refresh(philippe);
        assertNotEquals(philippe.getLastUpdated(), creationTime, extraMessage);
    }
}
