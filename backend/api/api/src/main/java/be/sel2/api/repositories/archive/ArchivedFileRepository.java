package be.sel2.api.repositories.archive;

import be.sel2.api.entities.archive.FileMetaDeleted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArchivedFileRepository extends JpaRepository<FileMetaDeleted, Long> {

    @Query("SELECT sum(fileSize) FROM FileMetaDeleted")
    Long getUsedStorageSpace();
}
