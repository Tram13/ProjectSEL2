package be.sel2.api.entities;

import be.sel2.api.serializers.EnumToLowercaseSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
public class Member implements StatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "organisationId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    private Organisation organisation;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    private UserInfo user;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private Boolean accepted;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld moet niet worden geüpdate.
    private Date created = new Date();

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld wordt automatisch geüpdatet.
    private Date lastUpdated = new Date();

    public Member() {
    }

    /**
     * Creates an entity {@link Member} with given parameters
     *
     * @param organisation the organisation the member is a part of
     * @param user         the user themselves
     * @param role         the user's role in the organisation, such as manager or member
     * @param accepted     the accepted state of the membership
     */
    public Member(Organisation organisation, UserInfo user, MemberRole role, Boolean accepted) {
        this.organisation = organisation;
        this.user = user;
        this.role = role;
        this.accepted = accepted;
    }

    @JsonSerialize(using = EnumToLowercaseSerializer.class)
    public enum MemberRole {
        MANAGER, MEMBER;

        public static MemberRole fromString(String val) {
            try {
                if (val.toLowerCase().equals(val)) {
                    return MemberRole.valueOf(val.toUpperCase());
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
        return "Member{" +
                "id=" + id +
                ", organisation=" + organisation +
                ", user=" + user +
                ", role=" + role +
                ", accepted=" + accepted +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id)
                && Objects.equals(organisation, member.organisation)
                && Objects.equals(user, member.user)
                && role == member.role
                && accepted == member.accepted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, organisation, user, role, accepted);
    }
}
