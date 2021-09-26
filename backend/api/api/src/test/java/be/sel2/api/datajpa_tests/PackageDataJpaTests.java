package be.sel2.api.datajpa_tests;

import be.sel2.api.entities.Package;
import be.sel2.api.entities.Service;
import be.sel2.api.entities.relations.PackageService;
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

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class PackageDataJpaTests {

    private Service getDefaultService1(boolean save) {
        Service service = new Service(
                "GeefAantalAanwezigheden",
                "Onderwijs",
                "Het opvragen van aantal halve dagen aanwezigheid van een persoon op basis van diens INSZ en het schooljaar.",
                false,
                new HashSet<>(Arrays.asList("AgODi", "VTC")),
                new HashSet<>(Arrays.asList(Service.DeliveryMethod.FTP, Service.DeliveryMethod.WEBSERVICE))
        );
        if (save) {
            servRepo.saveAndFlush(service);
        }
        return service;
    }

    private Service getDefaultService2(boolean save) {
        Service service = new Service(
                "GeefRapport",
                "Onderwijs",
                "Het opvragen van het rapport van een persoon op basis van diens INSZ en het schooljaar.",
                false,
                new HashSet<>(Arrays.asList("AgODi", "VTC")),
                new HashSet<>(Arrays.asList(Service.DeliveryMethod.FTP, Service.DeliveryMethod.WEBSERVICE))
        );
        if (save) {
            servRepo.saveAndFlush(service);
        }
        return service;
    }

    private Service getDefaultService3(boolean save) {
        Service service = new Service(
                "GeefEpc",
                "Energie",
                "Deze dienst geeft verschillende gegevens terug: gegevens van het EPC, zoals het attestnummer, bouwjaar, status, datum ingediend, ligging (kadaster, adres, etc.), documenten, personen, etc.",
                false,
                new HashSet<>(Collections.singletonList("VEA")),
                new HashSet<>(Arrays.asList(Service.DeliveryMethod.FTP, Service.DeliveryMethod.MO))
        );
        if (save) {
            servRepo.saveAndFlush(service);
        }
        return service;
    }

    private PackageService getDefaultPackageService1(Package pack, boolean save) {
        PackageService packServ = new PackageService(
                pack,
                getDefaultService1(save),
                "VTC",
                Service.DeliveryMethod.FTP
        );
        if (save) {
            packServRepo.saveAndFlush(packServ);
        }
        return packServ;
    }

    private PackageService getDefaultPackageService2(Package pack, boolean save) {
        PackageService packServ = new PackageService(
                pack,
                getDefaultService2(save),
                "VTC",
                Service.DeliveryMethod.FTP
        );
        if (save) {
            packServRepo.saveAndFlush(packServ);
        }
        return packServ;
    }

    private PackageService getDefaultPackageService3(Package pack, boolean save) {
        PackageService packServ = new PackageService(
                pack,
                getDefaultService3(save),
                "EPC",
                Service.DeliveryMethod.MO
        );
        if (save) {
            packServRepo.saveAndFlush(packServ);
        }
        return packServ;
    }

    private Package getDefaultPackage(boolean save) {
        Package pack = new Package();
        pack.setName("Onderwijs");
        pack.setDeprecated(false);
        if (save) {
            repo.saveAndFlush(pack);
        }
        return pack;
    }

    @Autowired
    private PackageRepository repo;
    @Autowired
    private PackageServiceRepository packServRepo;
    @Autowired
    private ServiceRepository servRepo;

    @Test
    void checkPackageEquals() {
        Package package1 = getDefaultPackage(true);
        getDefaultPackageService1(package1, true);
        getDefaultPackageService2(package1, true);

        Package package1DifferentId = getDefaultPackage(false);
        repo.saveAndFlush(package1DifferentId); // Afzonderlijk opgeslage, dus ander ID
        assertNotEquals(package1, package1DifferentId);

        Package package1ExactCopy = getDefaultPackage(true);
        getDefaultPackageService3(package1ExactCopy, true); // Equals zou niet mogen kijken naar de relaties buiten deze entiteit
        package1ExactCopy.setId(package1.getId()); // ID gelijk zetten
        assertEquals(package1, package1ExactCopy);

        Package packageCopy1 = getDefaultPackage(false);
        packageCopy1.setServices(package1.getServices());
        packageCopy1.setName("Financiën");
        packageCopy1.setId(package1.getId()); // ID gelijk zetten
        assertNotEquals(package1, packageCopy1);

        packageCopy1 = getDefaultPackage(false);
        packageCopy1.setServices(package1.getServices());
        packageCopy1.setDeprecated(true);
        packageCopy1.setId(package1.getId()); // ID gelijk zetten
        assertNotEquals(package1, packageCopy1);

        packageCopy1 = getDefaultPackage(false);
        packageCopy1.setServices(package1.getServices());
        packageCopy1.setDeprecated(true);
        packageCopy1.setId(package1.getId()); // ID gelijk zetten
        assertNotEquals(package1, packageCopy1);

        packageCopy1 = getDefaultPackage(false);
        packageCopy1.setServices(package1.getServices());
        packageCopy1.setDeprecated(true);
        packageCopy1.setId(package1.getId()); // ID gelijk zetten
        assertNotEquals(package1, packageCopy1);
    }

    @Test
    void checkSaveNewToRepository() {
        Package pack = getDefaultPackage(true);
        repo.saveAndFlush(pack);
        Package packCopy = repo.getOne(pack.getId());
        assertNotNull(packCopy);
        assertEquals(pack, packCopy);
    }

    @Test
    void checkSaveUpdateToPackageRepositoryIsNotEqualsToOldValue() {
        Package pack1 = getDefaultPackage(false);
        Package pack2 = getDefaultPackage(false);
        assertNull(pack1.getId());
        assertNull(pack2.getId());
        assertEquals(pack1, pack2);

        repo.saveAndFlush(pack1);
        repo.saveAndFlush(pack2);
        assertNotEquals(pack1, pack2);

        pack1.setName("Nieuwe naam");
        repo.saveAndFlush(pack1);
        Package pack1FromDatabase = repo.getOne(pack1.getId());
        assertEquals(pack1, pack1FromDatabase);
        assertNotEquals(pack1FromDatabase.getName(), pack2.getName());
    }

    @Test
    void checkSaveUpdateToRepositoryDoesUpdate() {
        Package pack = getDefaultPackage(true);
        Package packCopy = repo.getOne(pack.getId());
        assertNotNull(packCopy);
        assertEquals(pack, packCopy);
        packCopy.setName("Nieuwe packagenaam");
        repo.saveAndFlush(packCopy);

        assertEquals(pack.getId(), packCopy.getId());
        Package updatedPackage = repo.getOne(packCopy.getId());
        assertEquals("Nieuwe packagenaam", updatedPackage.getName());
    }

    @Test
    void checkDeleteFromRepositoryByID() {
        Package pack = getDefaultPackage(true);
        Package packCopy = repo.getOne(pack.getId());
        assertNotNull(packCopy);
        Long packId = pack.getId();
        repo.deleteById(packId);
        assertThrows(JpaObjectRetrievalFailureException.class, () -> repo.getOne(packId));
    }

    @Test
    void checkDeleteFromRepositoryByObject() {
        Package pack = getDefaultPackage(true);
        Package packCopy = repo.getOne(pack.getId());
        assertNotNull(packCopy);
        Long packId = pack.getId();
        repo.delete(pack);
        assertThrows(JpaObjectRetrievalFailureException.class, () -> repo.getOne(packId));
    }

    @Test
    void checkAddServicesSet() {
        Package pack = getDefaultPackage(true);
        pack.setServices(Set.of());
        getDefaultPackageService1(pack, true);
        getDefaultPackageService2(pack, true);
        repo.refresh(pack);

        assertNotEquals(0, pack.getServices().size());
        assertEquals(2, pack.getServices().size());
    }

    @Test
    void checkDeleteServicesSet() {
        Package pack = getDefaultPackage(true);
        PackageService packServ = getDefaultPackageService1(pack, true);
        pack.setServices(Set.of(packServ));
        repo.refresh(pack);
        assertEquals(1, pack.getServices().size());

        packServRepo.delete(packServ);
        packServRepo.flush();
        repo.refresh(pack);
        assertEquals(0, pack.getServices().size());
    }
}
