package be.sel2.api.datajpa_tests;

import be.sel2.api.entities.FileMeta;
import be.sel2.api.repositories.FileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // Set the active profile to use application-test.properties
@DataJpaTest
class FileDataJpaTests {
    @Autowired
    private FileRepository repo;

    private FileMeta getDefaultFileMeta() {
        return new FileMeta(
                "/data/files/bestand.pdf"
        );
    }

    @Test
    void checkFileDataEquals() {
        FileMeta file = getDefaultFileMeta();
        repo.save(file);

        FileMeta file2 = new FileMeta("/var/www/bestand.pdf");
        repo.save(file2);
        assertNotEquals(file, file2);

        FileMeta file3 = getDefaultFileMeta();
        assertNotEquals(file, file3); // IDs verschillen
        file3.setId(file.getId());
        assertEquals(file, file3);

        file3 = getDefaultFileMeta();
        file3.setId(file.getId());
        file3.setFileLocation("/var/www/bestand.pdf");
        assertNotEquals(file, file3);
    }

    @Test
    void checkSaveNewFileMetaToRepository() {
        FileMeta file = getDefaultFileMeta();
        assertNull(file.getId());
        repo.save(file);
        assertNotNull(file.getId());
        FileMeta fileCopy = repo.getOne(file.getId());
        assertNotNull(fileCopy);
        assertEquals(file, fileCopy);
    }

    @Test
    void checkSaveUpdateToOrganisationRepositoryIsEqual() {
        FileMeta file = getDefaultFileMeta();
        repo.save(file);
        assertEquals("/data/files/bestand.pdf", file.getFileLocation());
        file.setFileLocation("/var/www/bestand.pdf");
        repo.save(file);
        FileMeta fileCopy = repo.getOne(file.getId());
        assertNotEquals("/data/files/bestand.pdf", fileCopy.getFileLocation());
        assertEquals("/var/www/bestand.pdf", fileCopy.getFileLocation());
    }

    @Test
    void checkSaveUpdateNotEquals() {
        FileMeta file1 = getDefaultFileMeta();
        FileMeta file2 = getDefaultFileMeta();
        assertNull(file1.getId());
        assertNull(file2.getId());
        assertEquals(file1.getFileLocation(), file2.getFileLocation());
        repo.save(file1);
        repo.save(file2);
        assertNotEquals(file1, file2); // ID anders
        assertEquals(file1.getFileLocation(), file2.getFileLocation()); // fileLocation wel gelijk
        file1.setFileLocation("/var/www/bestand.pdf");
        assertNotEquals(file1.getFileLocation(), file2.getFileLocation());
    }

    @Test
    void checkDeleteFromRepositoryById() {
        FileMeta file = getDefaultFileMeta();
        repo.save(file);
        FileMeta fileCopy = repo.getOne(file.getId());
        assertNotNull(fileCopy);
        Long id = file.getId();
        repo.deleteById(id);
        assertThrows(JpaObjectRetrievalFailureException.class, () -> repo.getOne(id));
    }

    @Test
    void checkDeleteFromRepositoryByObject() {
        FileMeta file = getDefaultFileMeta();
        repo.save(file);
        FileMeta fileCopy = repo.getOne(file.getId());
        assertNotNull(fileCopy);
        Long id = file.getId();
        repo.delete(file);
        assertThrows(JpaObjectRetrievalFailureException.class, () -> repo.getOne(id));
    }
}
