package be.sel2.api.repositories;

import be.sel2.api.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * Repository making use of JPA specifications
 */
public interface UserRepository extends JpaRepository<UserInfo, Long>,
        JpaSpecificationExecutor<UserInfo>, RefreshRepository<UserInfo> {
}
