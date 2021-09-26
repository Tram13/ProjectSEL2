package be.sel2.api.repositories.archive;

import be.sel2.api.entities.archive.UserInfoDeleted;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivedUserRepository extends JpaRepository<UserInfoDeleted, Long> {
}
