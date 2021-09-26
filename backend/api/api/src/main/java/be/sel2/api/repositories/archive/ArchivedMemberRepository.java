package be.sel2.api.repositories.archive;

import be.sel2.api.entities.archive.MemberDeleted;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArchivedMemberRepository extends JpaRepository<MemberDeleted, Long> {

    List<MemberDeleted> findAllByOrganisation(Long organisation);
}
