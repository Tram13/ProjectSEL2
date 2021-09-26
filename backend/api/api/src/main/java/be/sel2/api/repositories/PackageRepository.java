package be.sel2.api.repositories;

import be.sel2.api.entities.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PackageRepository extends JpaRepository<Package, Long>,
        JpaSpecificationExecutor<Package>, RefreshRepository<Package> {
}
