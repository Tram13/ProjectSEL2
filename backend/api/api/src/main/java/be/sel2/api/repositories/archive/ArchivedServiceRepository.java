package be.sel2.api.repositories.archive;

import be.sel2.api.entities.archive.ServiceDeleted;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivedServiceRepository extends JpaRepository<ServiceDeleted, Long> {
}
