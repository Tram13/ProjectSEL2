package be.sel2.api.repositories;

import be.sel2.api.entities.relations.PackageService;
import be.sel2.api.entities.relations.PackageServiceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PackageServiceRepository extends JpaRepository<PackageService, PackageServiceId>,
        JpaSpecificationExecutor<PackageService>, RefreshRepository<PackageService> {
}
