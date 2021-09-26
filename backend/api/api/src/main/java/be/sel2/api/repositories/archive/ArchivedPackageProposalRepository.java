package be.sel2.api.repositories.archive;

import be.sel2.api.entities.archive.relations.PackageProposalDeleted;
import be.sel2.api.entities.archive.relations.PackageProposalDeletedId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArchivedPackageProposalRepository extends JpaRepository<PackageProposalDeleted, PackageProposalDeletedId> {

    List<PackageProposalDeleted> findAllByPack(Long pack);
}
