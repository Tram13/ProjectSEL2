package be.sel2.api.datajpa_tests;

import be.sel2.api.entities.Service;
import be.sel2.api.repositories.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // Set the active profile to use application-test.properties
@DataJpaTest
class ServiceDataJpaTests {
    @Autowired
    private ServiceRepository serviceRepository;

    private Service getDefaultService1() {
        return new Service(
                "GeefAantalAanwezigheden",
                "Onderwijs",
                "Het opvragen van aantal halve dagen aanwezigheid van een persoon op basis van diens INSZ en het schooljaar.",
                false,
                new HashSet<>(Arrays.asList("AgODi", "VTC")),
                new HashSet<>(Arrays.asList(Service.DeliveryMethod.FTP, Service.DeliveryMethod.WEBSERVICE))
        );
    }

    private Service getDefaultService2() {
        return new Service(
                "GeefEpc",
                "Energie",
                "Deze dienst geeft verschillende gegevens terug: gegevens van het EPC, zoals het attestnummer, bouwjaar, status, datum ingediend, ligging (kadaster, adres, etc.), documenten, personen, etc.",
                false,
                new HashSet<>(Collections.singletonList("VEA")),
                new HashSet<>(Arrays.asList(Service.DeliveryMethod.FTP, Service.DeliveryMethod.MO))
        );
    }

    @Test
    void checkServiceEquals() {
        Service service1 = getDefaultService1();
        serviceRepository.save(service1);

        Service service1DifferentId = getDefaultService1();
        serviceRepository.save(service1DifferentId); // Afzonderlijk opgeslagen, dus ander ID
        assertNotEquals(service1, service1DifferentId);

        Service service1ExactCopy = getDefaultService1(); // Dezelfde waarden als service 1, behalve ID
        service1ExactCopy.setId(service1.getId());  // Enkel nog ID gelijk zetten
        assertEquals(service1, service1ExactCopy);

        Service service2 = getDefaultService2(); // Dezelfde waarden als service 1, behalve ID
        serviceRepository.save(service2);
        assertNotEquals(service1, service2);

        Service serviceCopy1 = getDefaultService1();
        serviceCopy1.setName("GeefRapport");
        service2.setId(serviceCopy1.getId());  // ID gelijk zetten
        assertNotEquals(service1, serviceCopy1);

        serviceCopy1 = getDefaultService1();
        serviceCopy1.setDomain("Dossiers");
        service2.setId(serviceCopy1.getId());  // ID gelijk zetten
        assertNotEquals(service1, serviceCopy1);

        serviceCopy1 = getDefaultService1();
        serviceCopy1.setDescription("Deze dienst geeft de scores van de leerlingen terug");
        service2.setId(serviceCopy1.getId());  // ID gelijk zetten
        assertNotEquals(service1, serviceCopy1);

        serviceCopy1 = getDefaultService1();
        serviceCopy1.setNeedsPermissions(true);
        service2.setId(serviceCopy1.getId());  // ID gelijk zetten
        assertNotEquals(service1, serviceCopy1);

        serviceCopy1 = getDefaultService1();
        serviceCopy1.setSources(new HashSet<>(Collections.singletonList("FodFin")));
        service2.setId(serviceCopy1.getId());  // ID gelijk zetten
        assertNotEquals(service1, serviceCopy1);

        serviceCopy1 = getDefaultService1();
        serviceCopy1.setSources(new HashSet<>(Arrays.asList("VTC", "FodFin")));
        service2.setId(serviceCopy1.getId());  // ID gelijk zetten
        assertNotEquals(service1, serviceCopy1);

        serviceCopy1 = getDefaultService1();
        serviceCopy1.setDeliveryMethods(new HashSet<>(Arrays.asList(Service.DeliveryMethod.FTP, Service.DeliveryMethod.MO)));
        service2.setId(serviceCopy1.getId());  // ID gelijk zetten
        assertNotEquals(service1, serviceCopy1);
    }

    @Test
    void checkSaveNewToRepository() {
        Service service = getDefaultService1();
        serviceRepository.save(service);
        Service serviceCopy = serviceRepository.getOne(service.getId());
        assertNotNull(serviceCopy);
        assertEquals(service, serviceCopy);
    }

    @Test
    void checkSaveUpdateToServiceRepositoryIsNotEqualToOldValue() {
        Service service1 = getDefaultService1();
        Service service1Copy = getDefaultService1();
        assertNull(service1.getId());
        assertNull(service1Copy.getId());
        assertEquals(service1.getName(), service1Copy.getName());
        serviceRepository.save(service1);
        serviceRepository.save(service1Copy);
        assertNotEquals(service1, service1Copy); // ID is anders
        assertEquals(service1.getName(), service1Copy.getName()); // Maar naam blijft gelijk

        service1.setName("GeefRapporten");
        serviceRepository.save(service1);
        Service service1FromDatabase = serviceRepository.getOne(service1.getId());
        assertEquals(service1.getId(), service1FromDatabase.getId());
        assertNotEquals(service1FromDatabase.getName(), service1Copy.getName());
    }

    @Test
    void checkSaveUpdateToRepositoryDoesUpdate() {
        Service service = getDefaultService1();
        serviceRepository.save(service);

        Service serviceCopy = serviceRepository.getOne(service.getId());
        assertEquals(service, serviceCopy);
        serviceCopy.setName("GeefRapporten");
        serviceRepository.save(serviceCopy);

        Service rapportService = serviceRepository.getOne(serviceCopy.getId());
        assertEquals("GeefRapporten", rapportService.getName());
    }

    @Test
    void checkDeleteFromRepositoryById() {
        Service service = getDefaultService1();
        serviceRepository.save(service);
        Service serviceCopy = serviceRepository.getOne(service.getId());
        assertNotNull(serviceCopy);
        Long serviceId = service.getId();
        serviceRepository.deleteById(serviceId);
        assertThrows(JpaObjectRetrievalFailureException.class, () -> serviceRepository.getOne(serviceId));
    }

    @Test
    void checkDeleteFromRepositorByObject() {
        Service service = getDefaultService1();
        serviceRepository.save(service);
        Service serviceCopy = serviceRepository.getOne(service.getId());
        assertNotNull(serviceCopy);
        Long serviceId = service.getId();
        serviceRepository.delete(service);
        assertEquals("GeefAantalAanwezigheden", serviceCopy.getName());
        assertThrows(JpaObjectRetrievalFailureException.class, () -> serviceRepository.getOne(serviceId));
    }
}
