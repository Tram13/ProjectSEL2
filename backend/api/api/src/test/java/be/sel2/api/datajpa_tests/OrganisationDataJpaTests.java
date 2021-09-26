package be.sel2.api.datajpa_tests;

import be.sel2.api.entities.Organisation;
import be.sel2.api.repositories.OrganisationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // Set the active profile to use application-test.properties
@DataJpaTest
class OrganisationDataJpaTests {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Test
    void checkOrganisationEquals() {
        Organisation monarchy1 = new Organisation("Koningshuis België", "KoningKBO", "KoningOVO", "KoningNIS", "DienstServies");
        organisationRepository.save(monarchy1);

        Organisation monarchy2 = new Organisation("Koningshuis Nederland", "KoningKBO", "KoningOVO", "KoningNIS", "DienstServies"); // Andere organisatienaam
        organisationRepository.save(monarchy2);
        assertNotEquals(monarchy1, monarchy2);

        Organisation monarchy3 = new Organisation("Koningshuis België", "KoningKBO", "KoningOVO", "KoningNIS", "DienstServies"); // Kopie van monarchy1
        assertNotEquals(monarchy1, monarchy3); // ID, lastUpdated staan niet juist
        monarchy3.setId(monarchy1.getId());
        monarchy3.setLastUpdated(monarchy1.getLastUpdated());
        assertEquals(monarchy1, monarchy3); // ID? lastUpdated staan juist

        Organisation monarchy4 = new Organisation("Koningshuis België", "KoningKOB", "KoningOVO", "KoningNIS", "DienstServies"); // Ander KBO-Nummer
        monarchy4.setId(monarchy1.getId());
        monarchy4.setLastUpdated(monarchy1.getLastUpdated());
        assertNotEquals(monarchy1, monarchy4);

        Organisation monarchy5 = new Organisation("Koningshuis België", "KoningKBO", "KoningIVI", "KoningNIS", "DienstServies"); // Andere OVO-code
        monarchy5.setId(monarchy1.getId());
        monarchy5.setLastUpdated(monarchy1.getLastUpdated());
        assertNotEquals(monarchy1, monarchy5);

        Organisation monarchy6 = new Organisation("Koningshuis België", "KoningKBO", "KoningOVO", "KoningNAS", "DienstServies"); // Ander NIS-nummer
        monarchy6.setId(monarchy1.getId());
        monarchy6.setLastUpdated(monarchy1.getLastUpdated());
        assertNotEquals(monarchy1, monarchy6);

        Organisation monarchy7 = new Organisation("Koningshuis België", "KoningKBO", "KoningOVO", "KoningNAS", "ServiesDienst"); // Andere service provider
        monarchy7.setId(monarchy1.getId());
        monarchy7.setLastUpdated(monarchy1.getLastUpdated());
        assertNotEquals(monarchy1, monarchy7);
    }

    @Test
    void checkSaveNewToOrganisationRepository() {
        Organisation monarchy = new Organisation("Koningshuis België", "KoningKBO", "KoningOVO", "KoningNIS", "DienstServies");
        assertNull(monarchy.getId());
        organisationRepository.save(monarchy);
        assertNotNull(monarchy.getId());
        Organisation copyMonarchy = organisationRepository.getOne(monarchy.getId());
        assertNotNull(copyMonarchy);
        assertEquals(monarchy, copyMonarchy);
    }

    @Test
    void checkSaveUpdateToOrganisationRepositoryIsEqual() {
        Organisation monarchy = new Organisation("Koningshuis België", "KoningKBO", "KoningOVO", "KoningNIS", "DienstServies");
        organisationRepository.save(monarchy);
        monarchy.setOrganisationName("House of Windsor");
        organisationRepository.save(monarchy); // Save is eigenlijk overbodig: https://stackoverflow.com/questions/35845407/does-jpa-repository-automatically-update-the-object-when-a-setter-is-called
        Organisation copyMonarchy = organisationRepository.getOne(monarchy.getId());
        assertNotEquals("Koningshuis België", copyMonarchy.getOrganisationName());
        assertEquals("House of Windsor", copyMonarchy.getOrganisationName());
    }

    @Test
    void checkSaveUpdateNotEquals() {
        Organisation monarchy1 = new Organisation("Koningshuis België", "KoningKBO", "KoningOVO", "KoningNIS", "DienstServies");
        Organisation monarchy2 = new Organisation("Koningshuis België", "KoningKBO", "KoningOVO", "KoningNIS", "DienstServies");
        assertNull(monarchy1.getId());
        assertNull(monarchy2.getId());
        assertEquals(monarchy1.getOrganisationName(), monarchy2.getOrganisationName());
        organisationRepository.save(monarchy1);
        organisationRepository.save(monarchy2);
        assertNotEquals(monarchy1, monarchy2); // ID is anders
        assertEquals(monarchy1.getOrganisationName(), monarchy2.getOrganisationName()); // De naam blijft wel gelijk
        monarchy1.setOrganisationName("Koningshuis Nederland");
        assertNotEquals(monarchy1.getOrganisationName(), monarchy2.getOrganisationName()); // De naam is aangepast
    }

    @Test
    void checkDeleteFromOrganisationRepositoryById() {
        Organisation monarchy = new Organisation("Koningshuis België", "KoningKBO", "KoningOVO", "KoningNIS", "DienstServies");
        organisationRepository.save(monarchy);
        Organisation copyMonarchy = organisationRepository.getOne(monarchy.getId());
        assertNotNull(copyMonarchy);
        Long monarchyId = monarchy.getId();
        organisationRepository.deleteById(monarchy.getId());
        assertThrows(JpaObjectRetrievalFailureException.class, () -> organisationRepository.getOne(monarchyId));
    }


    @Test
    void checkDeleteFromOrganisationRepositoryByObject() {
        Organisation monarchy = new Organisation("Koningshuis België", "KoningKBO", "KoningOVO", "KoningNIS", "DienstServies");
        organisationRepository.save(monarchy);
        Organisation copyMonarchy = organisationRepository.getOne(monarchy.getId());
        assertNotNull(copyMonarchy);
        Long monarchyId = monarchy.getId();
        organisationRepository.delete(monarchy);
        assertEquals("Koningshuis België", copyMonarchy.getOrganisationName());
        assertThrows(JpaObjectRetrievalFailureException.class, () -> organisationRepository.getOne(monarchyId));
    }
}