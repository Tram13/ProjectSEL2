package be.sel2.api.entities.archive.relations;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
public class PackageProposalDeletedId implements Serializable {
    private Long pack;
    private Long proposal;
    private Date deletedOn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PackageProposalDeletedId)) return false;
        PackageProposalDeletedId that = (PackageProposalDeletedId) o;
        return pack.equals(that.pack) &&
                proposal.equals(that.proposal) &&
                deletedOn.equals(that.deletedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pack, proposal);
    }
}
