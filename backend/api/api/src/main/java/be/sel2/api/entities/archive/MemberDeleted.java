package be.sel2.api.entities.archive;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class MemberDeleted extends ArchivedEntity {

    private Long organisation;
}
