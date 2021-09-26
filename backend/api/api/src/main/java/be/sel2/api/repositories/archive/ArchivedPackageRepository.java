package be.sel2.api.repositories.archive;

import be.sel2.api.entities.archive.PackageDeleted;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivedPackageRepository extends JpaRepository<PackageDeleted, Long> {
}
