package be.sel2.api.entities;

import be.sel2.api.entities.relations.PackageService;
import be.sel2.api.serializers.PackageServiceSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter //This adds all the getters & setters for us
public class Package implements StatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private @NotNull String name;
    private Boolean deprecated = false;

    @OneToMany(mappedBy = "pack")
    @JsonSerialize(using = PackageServiceSerializer.class)
    private Set<PackageService> services;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld moet niet worden geüpdate.
    private Date created = new Date();

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld wordt automatisch geüpdatet.
    private Date lastUpdated = new Date();

    public Package() {
    }

    /**
     * Creates a new Entity {@link Package} with the given parameters
     *
     * @param name     The service's name
     * @param services A set of services this package contains
     */
    public Package(String name, Set<PackageService> services) {
        this.name = name;
        this.services = services;
    }

    // Helper functions


    @Override
    public String toString() {
        return "Package{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deprecated=" + deprecated +
                ", services=" + services +
                ", created=" + created +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Package aPackage = (Package) o;
        return Objects.equals(id, aPackage.id) && Objects.equals(name, aPackage.name) && Objects.equals(deprecated, aPackage.deprecated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, deprecated);
    }
}


