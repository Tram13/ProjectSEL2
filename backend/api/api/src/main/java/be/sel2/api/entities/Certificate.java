package be.sel2.api.entities;

import be.sel2.api.serializers.FileToHrefSerializer;
import be.sel2.api.serializers.ReducedProposalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter //This adds all the getters & setters for us
public class Certificate implements StatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "fileid", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    @JsonSerialize(using = FileToHrefSerializer.class)
    private @NotNull FileMeta file;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld moet niet worden geüpdatet.
    private Date created = new Date();

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld wordt automatisch geüpdatet.
    private Date lastUpdated = new Date();

    @JsonSerialize(using = ReducedProposalSerializer.class)
    @OneToOne
    @JoinColumn(name = "proposalid", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    private @NotNull Proposal proposal;

    public Certificate() {
    }

    public Certificate(@NotNull FileMeta file, @NotNull Proposal proposal) {
        this.file = file;
        this.proposal = proposal;
    }

    @Override
    public String toString() {
        return "Certificate{" +
                "id=" + id +
                ", file=" + file +
                ", created=" + created +
                ", lastUpdated=" + lastUpdated +
                ", proposal=" + proposal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Certificate that = (Certificate) o;
        return Objects.equals(id, that.id) && Objects.equals(file, that.file) && Objects.equals(proposal, that.proposal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, file, proposal);
    }
}
