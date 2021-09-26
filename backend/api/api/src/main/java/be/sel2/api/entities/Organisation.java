package be.sel2.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter //This adds all the getters & setters for us
public class Organisation implements StatisticsEntity {
    // Velden
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private @NotNull String organisationName;
    private @NotNull String kboNumber;
    private String ovoCode;
    private String nisNumber;
    private @NotNull String serviceProvider;

    private Boolean approved = false;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld moet niet worden geüpdate.
    private Date created = new Date();

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een blanco date object toegevoegd worden in de databank. Dit veld wordt automatisch geüpdatet.
    private Date lastUpdated = new Date();


    @OneToMany(mappedBy = "organisation")
    @JsonIgnore
    private Set<Member> members = new HashSet<>();


    public Organisation() {  // @Entity heeft een lege constructor nodig
    }

    /**
     * Creates a new Entity {@link Organisation} with the given parameters
     *
     * @param organisationName The organisation's name
     * @param kboNumber        The organisation's KBO number
     * @param ovoCode          The organisation's OVO code
     * @param nisNumber        The organisation's NIS number
     * @param serviceProvider  The organisation's service provide
     */
    public Organisation(String organisationName, String kboNumber, String ovoCode, String nisNumber, String serviceProvider) {
        this.organisationName = organisationName;
        this.kboNumber = kboNumber;
        this.ovoCode = ovoCode;
        this.nisNumber = nisNumber;
        this.serviceProvider = serviceProvider;
    }

    // Helper functions


    @Override
    public String toString() {
        return "Organisation{" +
                "id=" + id +
                ", organisationName='" + organisationName + '\'' +
                ", kboNumber='" + kboNumber + '\'' +
                ", ovoCode='" + ovoCode + '\'' +
                ", nisNumber='" + nisNumber + '\'' +
                ", serviceProvider='" + serviceProvider + '\'' +
                ", approved=" + approved +
                ", created=" + created +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organisation that = (Organisation) o;
        return Objects.equals(id, that.id) && Objects.equals(organisationName, that.organisationName) && Objects.equals(kboNumber, that.kboNumber) && Objects.equals(ovoCode, that.ovoCode) && Objects.equals(nisNumber, that.nisNumber) && Objects.equals(serviceProvider, that.serviceProvider) && Objects.equals(approved, that.approved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, organisationName, kboNumber, ovoCode, nisNumber, serviceProvider, approved);
    }
}
