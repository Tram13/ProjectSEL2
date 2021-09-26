package be.sel2.api.entities;

import be.sel2.api.serializers.EnumToLowercaseSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Relation(collectionRelation = "userList") //Setup correct name in CollectionModel JSON
@Getter
@Setter //This adds all the getters & setters for us
public class UserInfo implements StatisticsEntity {
    // Velden
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private @NotNull String firstName;
    private @NotNull String lastName;
    private @NotNull String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // NEVER return the password of a user
    private @NotNull String password;
    @Enumerated(EnumType.STRING)
    private @NotNull Userrole role;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Member> memberships = new HashSet<>();

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld moet niet worden geüpdate.
    private Date created = new Date();

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld wordt automatisch geüpdatet.
    private Date lastUpdated = new Date();

    public UserInfo() { // @Entity needs an empty constructor
    }

    /**
     * Creates an entity {@link UserInfo} with given parameters
     *
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param email     the user's email address, must be unique
     * @param password  the user's password
     * @param role      the user's role, such as admin or customer
     */
    public UserInfo(String firstName, String lastName, String email, String password, Userrole role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email.toLowerCase();
        this.password = password;
        this.role = role;
    }

    // Get all organisations for this user, GETTER only
    @JsonIgnore
    public Set<Organisation> getOrganisations() {
        return memberships
                .stream().map(Member::getOrganisation)
                .collect(Collectors.toSet());
    }

    // Ieder account heeft exact 1 van onderstaande rollen
    @JsonSerialize(using = EnumToLowercaseSerializer.class)
    public enum Userrole {
        CUSTOMER,
        EMPLOYEE,
        ADMIN;

        public String toAuthorizationRole() {
            return "ROLE_" + this;
        }

        public static Userrole fromString(String val) {
            try {
                if (val.toLowerCase().equals(val)) {
                    return Userrole.valueOf(val.toUpperCase());
                } else {
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException | NullPointerException e) {
                return null;
            }
        }
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", created=" + created +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return Objects.equals(id, userInfo.id) && Objects.equals(firstName, userInfo.firstName) && Objects.equals(lastName, userInfo.lastName) && Objects.equals(email, userInfo.email) && Objects.equals(password, userInfo.password) && role == userInfo.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, password, role);
    }
}
