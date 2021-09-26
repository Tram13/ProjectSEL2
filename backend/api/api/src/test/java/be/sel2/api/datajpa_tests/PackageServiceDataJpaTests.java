package be.sel2.api.datajpa_tests;


import be.sel2.api.entities.Package;
import be.sel2.api.entities.Service;
import be.sel2.api.entities.relations.PackageService;
import be.sel2.api.entities.relations.PackageServiceId;
import be.sel2.api.repositories.PackageRepository;
import be.sel2.api.repositories.PackageServiceRepository;
import be.sel2.api.repositories.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // Set the active profile to use application-test.properties
@DataJpaTest
class PackageServiceDataJpaTests {
    @Autowired
    private PackageServiceRepository repo;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private PackageRepository packRepo;

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

    private Package getDefaultPackage1() {
        Package pack = new Package();
        pack.setName("Onderwijs");
        pack.setDeprecated(false);
        return pack;
    }

    private Package getDefaultPackage2() {
        Package pack = new Package();
        pack.setName("Financiën");
        pack.setDeprecated(true);
        return pack;
    }

    private PackageService getDefaultPackageService(Package pack, Service serv) {
        return new PackageService(
                pack,
                serv,
                "FOD FIN",
                Service.DeliveryMethod.MO
        );
    }

    @Test
    void checkPackageServiceEquals() {
        Service serv1 = getDefaultService1();
        serviceRepository.save(serv1);
        Package pack = getDefaultPackage1();
        packRepo.save(pack);
        PackageService packServ = getDefaultPackageService(pack, serv1);
        repo.saveAndFlush(packServ);

        // Willekeurig gekozen andere klasse
        String randomObject = "Random object does not equal";
        assertNotEquals(packServ, randomObject);

        // Compleet verschillende PackageService
        Service serv2 = getDefaultService2();
        serviceRepository.save(serv1);
        Package pack2 = getDefaultPackage2();
        packRepo.save(pack2);
        PackageService packServ2 = getDefaultPackageService(pack, serv2);
        assertNotEquals(packServ, packServ2);

        // Zelfde parameters, dus moet gelijk zijn
        PackageService packServCopy = getDefaultPackageService(pack, serv1);
        assertEquals(packServ, packServCopy);

        // Service verschillend
        PackageService ps3 = getDefaultPackageService(pack, serv2);
        assertNotEquals(packServ, ps3);

        // Package verschillend
        PackageService ps4 = getDefaultPackageService(pack2, serv1);
        assertNotEquals(packServ, ps4);

        // Source verschillend
        PackageService ps5 = new PackageService(pack, serv1, "Andere Source", Service.DeliveryMethod.MO);
        assertNotEquals(packServ, ps5);

        // DeliveryMethod verschillend
        PackageService ps6 = new PackageService(pack, serv1, "FOD FIN", Service.DeliveryMethod.PUB);
        assertNotEquals(packServ, ps6);
    }

    @Test
    void checkSaveNewPackageServiceToRepository() {
        Service serv1 = getDefaultService1();
        serviceRepository.save(serv1);
        Package pack = getDefaultPackage1();
        packRepo.save(pack);
        assertNull(pack.getServices());

        PackageService packServ = getDefaultPackageService(pack, serv1);
        repo.saveAndFlush(packServ);
        PackageService packServCopy = repo.getOne(packServ.getId());
        assertNotNull(packServCopy);
        assertEquals(packServ, packServCopy);
    }

    @Test
    void checkSaveUpdateToRepository() {
        Service serv1 = getDefaultService1();
        serviceRepository.save(serv1);
        Package pack = getDefaultPackage1();
        packRepo.save(pack);
        assertNull(pack.getServices());

        PackageService packServ = getDefaultPackageService(pack, serv1);
        repo.saveAndFlush(packServ);

        assertEquals(Service.DeliveryMethod.MO, packServ.getDeliveryMethod());

        packServ.setDeliveryMethod(Service.DeliveryMethod.PUB);
        repo.save(packServ);
        PackageService packServCopy = repo.getOne(packServ.getId());
        assertNotEquals(Service.DeliveryMethod.MO, packServCopy.getDeliveryMethod());
        assertEquals(Service.DeliveryMethod.PUB, packServCopy.getDeliveryMethod());
    }

    @Test
    void checkSaveUpdateNotEquals() {
        Service serv1 = getDefaultService1();
        serviceRepository.save(serv1);
        Package pack = getDefaultPackage1();
        packRepo.save(pack);
        assertNull(pack.getServices());

        PackageService ps1 = getDefaultPackageService(pack, serv1);
        PackageService ps2 = getDefaultPackageService(pack, serv1);
        repo.save(ps1);
        repo.saveAndFlush(ps2);
        assertEquals(ps1.getDeliveryMethod(), ps2.getDeliveryMethod());
        ps1.setDeliveryMethod(Service.DeliveryMethod.WEBSERVICE);
        repo.saveAndFlush(ps1);
        assertNotEquals(ps1.getDeliveryMethod(), ps2.getDeliveryMethod());
    }

    @Test
    void checkDeleteFromRepositoryById() {
        Service serv1 = getDefaultService1();
        serviceRepository.save(serv1);
        Package pack = getDefaultPackage1();
        packRepo.save(pack);
        assertNull(pack.getServices());

        PackageService packServ = getDefaultPackageService(pack, serv1);
        repo.saveAndFlush(packServ);

        PackageService packServCopy = repo.getOne(packServ.getId());
        assertNotNull(packServCopy);
        PackageServiceId packServId = packServ.getId();
        repo.deleteById(packServId);
        assertThrows(JpaObjectRetrievalFailureException.class, () -> repo.getOne(packServId));
    }

    @Test
    void checkDeleteFromRepositoryByObject() {
        Service serv1 = getDefaultService1();
        serviceRepository.save(serv1);
        Package pack = getDefaultPackage1();
        packRepo.save(pack);
        assertNull(pack.getServices());

        PackageService packServ = getDefaultPackageService(pack, serv1);
        repo.saveAndFlush(packServ);

        PackageService packServCopy = repo.getOne(packServ.getId());
        assertNotNull(packServCopy);
        PackageServiceId packServId = packServ.getId();
        repo.delete(packServ);
        assertThrows(JpaObjectRetrievalFailureException.class, () -> repo.getOne(packServId));
    }

    @Test
    void creatingPackageServiceDoesCreateServicesSetInPackage() {
        Service serv1 = getDefaultService1();
        serviceRepository.save(serv1);
        Package pack = getDefaultPackage1();
        packRepo.save(pack);
        assertNull(pack.getServices());

        PackageService packServ = getDefaultPackageService(pack, serv1);
        repo.saveAndFlush(packServ);

        packRepo.refresh(pack);
        assertNotNull(pack.getServices());
        assertEquals(1, pack.getServices().size());
        assertTrue(pack.getServices().stream().findFirst().isPresent());
        assertEquals(serv1, pack.getServices().stream().findFirst().get().getService());
    }

    @Test
    void creatingMultiplePackageServiceDoesCreateServicesSetInPackage() {
        Service serv1 = getDefaultService1();
        serviceRepository.save(serv1);
        Service serv2 = getDefaultService2();
        serviceRepository.save(serv2);
        Package pack = getDefaultPackage1();
        packRepo.save(pack);
        assertNull(pack.getServices());

        PackageService packServ1 = getDefaultPackageService(pack, serv1);
        repo.save(packServ1);

        PackageService packServ2 = getDefaultPackageService(pack, serv2);
        repo.saveAndFlush(packServ2);

        packRepo.refresh(pack);

        assertNotNull(pack.getServices());
        assertNotEquals(0, pack.getServices().size());
        assertEquals(2, pack.getServices().size());
        Set<Service> servSet = pack.getServices().stream().map(PackageService::getService).collect(Collectors.toSet());
        Set<Service> servSetOriginal = Set.of(serv1, serv2);
        assertEquals(servSet, servSetOriginal);
    }

    @Test
    void deletePackageServiceAlsoDeletesFromSet() {
        Service serv1 = getDefaultService1();
        serviceRepository.save(serv1);
        Service serv2 = getDefaultService2();
        serviceRepository.save(serv2);
        Package pack = getDefaultPackage1();
        packRepo.save(pack);

        PackageService packServ1 = getDefaultPackageService(pack, serv1);
        repo.save(packServ1);
        PackageService packServ2 = getDefaultPackageService(pack, serv2);
        repo.saveAndFlush(packServ2);
        packRepo.refresh(pack);
        assertEquals(2, pack.getServices().size());

        repo.delete(packServ1);
        repo.flush();
        packRepo.refresh(pack);
        assertEquals(1, pack.getServices().size());
        assertTrue(pack.getServices().stream().findFirst().isPresent());
        PackageService packServ2Copy = pack.getServices().stream().findFirst().get();
        assertEquals(packServ2, packServ2Copy);
    }

    @Test
    void updatePackageServiceAlsoUpdatesPackage() {
        Service serv1 = getDefaultService1();
        serviceRepository.save(serv1);
        Service serv1Original = getDefaultService1();
        assertEquals(serv1Original.getSources(), serv1.getSources());
        Package pack = getDefaultPackage1();
        packRepo.save(pack);

        PackageService packServ1 = getDefaultPackageService(pack, serv1);
        repo.saveAndFlush(packServ1);
        packRepo.refresh(pack);
        assertTrue(pack.getServices().stream().findFirst().isPresent());
        PackageService packServCopy = pack.getServices().stream().findFirst().get();
        assertEquals(serv1, packServCopy.getService());

        // Onrechtstreeks aanpassen van PackageService
        serv1.setSources(Stream.of("Aangepaste dienst").collect(Collectors.toCollection(HashSet::new)));
        serviceRepository.saveAndFlush(serv1);
        packRepo.refresh(pack);

        // Controle of de aanpassing propageert naar PackageService
        assertTrue(pack.getServices().stream().findFirst().isPresent());
        packServCopy = pack.getServices().stream().findFirst().get();
        assertNotEquals(serv1Original.getSources(), packServCopy.getService().getSources()); // Oude waarde
        assertEquals(serv1.getSources(), packServCopy.getService().getSources()); // Nieuwe waarde
    }
}
