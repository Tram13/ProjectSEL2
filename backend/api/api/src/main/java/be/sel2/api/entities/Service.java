package be.sel2.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// Stelt een MAGDA-dienst voor
@Entity
@Getter
@Setter //This adds all the getters & setters for us
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Service implements StatisticsEntity {
    // Velden
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private @NotNull String name;
    private Boolean deprecated = false;
    private @NotNull String domain;
    private @NotNull String description;
    private @NotNull Boolean needsPermissions;

    @ElementCollection
    @CollectionTable(
            name = "ServiceSource",
            joinColumns = @JoinColumn(name = "serviceId", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    )
    @Column(name = "source")
    private Set<String> sources = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "ServiceDeliveryMethod",
            joinColumns = @JoinColumn(name = "serviceId", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    )
    @Column(name = "deliveryMethod")
    @Enumerated(EnumType.STRING)
    private Set<DeliveryMethod> deliveryMethods = new HashSet<>();

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld moet niet worden geüpdate.
    private Date created = new Date();

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld wordt automatisch geüpdatet.
    private Date lastUpdated = new Date();


    public Service() {
    }

    /**
     * Creates a new Entity {@link Service} with the given parameters
     *
     * @param name             The service's name
     * @param domain           The service's domain
     * @param description      The service's description
     * @param needsPermissions A bool indicating whether this service needs permissions
     * @param sources          The organisation that provides this service
     * @param deliveryMethods  The way this service will be accessed
     */
    public Service(String name, String domain, String description, Boolean needsPermissions, Set<String> sources, Set<DeliveryMethod> deliveryMethods) {
        this.name = name;
        this.domain = domain;
        this.description = description;
        this.needsPermissions = needsPermissions;
        this.sources = sources;
        this.deliveryMethods = deliveryMethods;
    }

    public enum DeliveryMethod {
        FTP, WEBSERVICE, MO, PUB;

        public static DeliveryMethod fromString(String val) throws IllegalArgumentException {
            try {
                return DeliveryMethod.valueOf(val.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                return null;
            }
        }
    }

    // Helper methods


    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deprecated=" + deprecated +
                ", domain='" + domain + '\'' +
                ", description='" + description + '\'' +
                ", needsPermissions=" + needsPermissions +
                ", sources=" + sources +
                ", deliveryMethods=" + deliveryMethods +
                ", created=" + created +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(id, service.id) && Objects.equals(name, service.name) && Objects.equals(deprecated, service.deprecated) && Objects.equals(domain, service.domain) && Objects.equals(description, service.description) && Objects.equals(needsPermissions, service.needsPermissions) && Objects.equals(sources, service.sources) && Objects.equals(deliveryMethods, service.deliveryMethods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, deprecated, domain, description, needsPermissions);
    }
}
