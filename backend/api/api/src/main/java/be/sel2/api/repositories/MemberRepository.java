package be.sel2.api.repositories;

import be.sel2.api.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MemberRepository extends JpaRepository<Member, Long>,
        JpaSpecificationExecutor<Member> {
}
