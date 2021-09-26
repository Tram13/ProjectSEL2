package be.sel2.api.entities;

import be.sel2.api.serializers.FileToHrefSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Permission implements StatisticsEntity {
    // Velden
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private @NotNull String name;
    private @NotNull String description;
    private @NotNull String code;
    private String link;
    @OneToOne
    @JoinColumn(name = "proof", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    @JsonSerialize(using = FileToHrefSerializer.class)
    private FileMeta proof;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld moet niet worden geüpdate.
    private Date created = new Date();

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld wordt automatisch geüpdatet.
    private Date lastUpdated = new Date();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "organisationId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    private Organisation organisation;

    public Permission() {
    }

    /**
     * Creates a new entity {@link Permission} with the given parameters
     *
     * @param name         The permission's name
     * @param description  The permission's description
     * @param code         The permission's code
     * @param link         The permission's link
     * @param proof        The permission's proof
     * @param organisation The permission's related organisation
     */
    public Permission(String name, String description, String code, String link, FileMeta proof, Organisation organisation) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.link = link;
        this.proof = proof;
        this.organisation = organisation;
    }

    // Helper functions


    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                ", link='" + link + '\'' +
                ", proof=" + proof +
                ", created=" + created +
                ", lastUpdated=" + lastUpdated +
                ", organisation=" + organisation +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(code, that.code) && Objects.equals(link, that.link) && Objects.equals(proof, that.proof) && Objects.equals(organisation, that.organisation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, code, link, proof, organisation);
    }
}
