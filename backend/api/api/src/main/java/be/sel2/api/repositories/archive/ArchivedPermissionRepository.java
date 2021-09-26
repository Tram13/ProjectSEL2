package be.sel2.api.repositories.archive;

import be.sel2.api.entities.archive.PermissionDeleted;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArchivedPermissionRepository extends JpaRepository<PermissionDeleted, Long> {

    List<PermissionDeleted> findAllByOrganisation(Long organisation);
}
