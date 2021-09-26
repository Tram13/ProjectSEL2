package be.sel2.api.repositories;

import be.sel2.api.entities.FileMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface FileRepository extends JpaRepository<FileMeta, Long>,
        JpaSpecificationExecutor<FileMeta> {

    @Query("SELECT sum(fileSize) FROM FileMeta")
    Long getUsedStorageSpace();
}
