package be.sel2.api.entities;

import be.sel2.api.entities.relations.ContactProposal;
import be.sel2.api.entities.relations.ProposalService;
import be.sel2.api.models.BooleanModel;
import be.sel2.api.serializers.EnumToLowercaseSerializer;
import be.sel2.api.serializers.FileToHrefSerializer;
import be.sel2.api.serializers.PackageListSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter //This adds all the getters & setters for us
public class Proposal implements StatisticsEntity {
    // Velden
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private @NotNull String name;
    @Enumerated(EnumType.STRING)
    private ProposalStatus status;

    // Deadlines
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deadline;
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date legalDeadline;
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date tiDeadline;
    private String explanationDeadline;

    private String businessContext;
    private String legalContext;

    private String functionalSetup;
    private String technicalSetup;

    // Data request information
    @Enumerated(EnumType.STRING)
    private BooleanModel requiresPersonalData;
    private String purposeRequestedData;

    // Extension data
    @Enumerated(EnumType.STRING)
    private BooleanModel extensionPreviousProposal;
    private String originalTanNumber;
    private String originalUri;
    private String originalLegalContext;

    // ftp account
    private String ftpAccount;

    // Estimated requests information
    @Enumerated(EnumType.STRING)
    private BooleanModel requestsAreSpread;
    @Enumerated(EnumType.STRING)
    private NumberOfRequests estimatedNumberOfRequests;

    // Feedback
    private String feedback;

    // Inspection truth value
    @Enumerated(EnumType.STRING)
    private BooleanModel inspection;

    // Architecture visualization
    private String architectureVisualizationExplanation;
    @OneToOne
    @JoinColumn(name = "architectureVisualizationId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    @JsonSerialize(using = FileToHrefSerializer.class)
    private FileMeta architectureVisualization;

    // Related organisations
    @ManyToOne
    @JoinColumn(name = "organisationId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    private @NotNull Organisation organisation;
    // Related services & contacts
    @OneToMany(mappedBy = "proposal")
    private Set<ProposalService> services;
    @OneToMany(mappedBy = "proposal")
    private Set<ContactProposal> contacts;
    @ManyToMany
    @JoinTable(
            name = "PackageProposal",
            joinColumns = {@JoinColumn(name = "proposalId", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))},
            inverseJoinColumns = {@JoinColumn(name = "packageId", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))}
    )
    // Related packages
    @JsonSerialize(using = PackageListSerializer.class)
    private Set<Package> packages;

    @OneToOne
    @JoinColumn(name = "authorizationPersonalDataId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    @JsonSerialize(using = FileToHrefSerializer.class)
    private FileMeta authorizationPersonalData;
    @Enumerated(EnumType.STRING)
    private BooleanModel ftp;
    private String externIpTest;
    private String externIpProd;
    private String externIpRangeTest;
    private String externIpRangeProd;
    @OneToOne
    @JoinColumn(name = "sshKeyId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    @JsonSerialize(using = FileToHrefSerializer.class)
    private FileMeta sshKey;
    @Enumerated(EnumType.STRING)
    private BooleanModel reuseUri;
    @Enumerated(EnumType.STRING)
    private BooleanModel reuseLegalContext;
    @Enumerated(EnumType.STRING)
    private BooleanModel reuseFtpAccount;
    @Enumerated(EnumType.STRING)
    private BooleanModel reusePreviousProposal;
    private String cnPersonalData;
    private String cnRepertorium;
    private String cnOther;
    @Enumerated(EnumType.STRING)
    private BooleanModel magdaMessages;
    @Enumerated(EnumType.STRING)
    private OnlineOption onlineOption;
    @OneToOne
    @JoinColumn(name = "cooperationAgreementId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    @JsonSerialize(using = FileToHrefSerializer.class)
    private FileMeta cooperationAgreement;

    @ElementCollection
    @CollectionTable(
            name = "ProposalOption",
            joinColumns = @JoinColumn(name = "proposalId", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    )
    @Column(name = "option")
    @Enumerated(EnumType.STRING)
    private Set<EboxOption> options;

    @OneToOne
    @JoinColumn(name = "processingAgreementId", referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
    @JsonSerialize(using = FileToHrefSerializer.class)
    private FileMeta processingAgreement;
    private Long peaks;


    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld moet niet worden geüpdate.
    private Date created = new Date();

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    // Er moet een default date object toegevoegd worden in de databank. Dit veld wordt automatisch geüpdatet.
    private Date lastUpdated = new Date();

    public Proposal() {
        // Een @Entity-klasse heeft een lege constructor nodig
    }

    // Iedere aanvraag heeft exact 1 van onderstaande statussen
    @JsonSerialize(using = EnumToLowercaseSerializer.class)
    public enum ProposalStatus {
        DRAFT, COMPLETED, IN_REVIEW,
        PENDING_FEEDBACK, ACCEPTED, DENIED,
        CANCELLED;

        public static ProposalStatus fromString(String val) {
            try {
                return ProposalStatus.valueOf(val.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                return ProposalStatus.DRAFT;
            }

        }
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public enum NumberOfRequests {
        LOW("<100K"),
        MID("100K-1M"),
        HIGH("1M-5M"),
        VERY_HIGH(">5M");

        private final String range;

        NumberOfRequests(String range) {
            this.range = range;
        }

        @Override
        public String toString() {
            return range;
        }

        public static NumberOfRequests fromString(String val) {
            if (val == null) {
                return null;
            }

            List<NumberOfRequests> matches = Arrays.stream(NumberOfRequests.values())
                    .filter(v -> v.toString().equals(val))
                    .collect(Collectors.toList());

            if (matches.isEmpty()) {
                throw new IllegalArgumentException();
            }
            return matches.get(0);
        }
    }

    public enum OnlineOption {
        WEB_SERVICE,
        MAGDA_ONLINE_PRO;

        public static OnlineOption fromString(String val) {
            try {
                return OnlineOption.valueOf(val.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                return null;
            }
        }
    }

    public enum EboxOption {
        EBOX_BURGER,
        EBOX_ONDERNEMER,
        BRIEF;

        public static EboxOption fromString(String val) {
            try {
                return EboxOption.valueOf(val.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                return null;
            }

        }
    }


    @Override
    public String toString() {
        return "Proposal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", deadline=" + deadline +
                ", legalDeadline=" + legalDeadline +
                ", tiDeadline=" + tiDeadline +
                ", explanationDeadline='" + explanationDeadline + '\'' +
                ", businessContext='" + businessContext + '\'' +
                ", legalContext='" + legalContext + '\'' +
                ", functionalSetup='" + functionalSetup + '\'' +
                ", technicalSetup='" + technicalSetup + '\'' +
                ", requiresPersonalData=" + requiresPersonalData +
                ", purposeRequestedData='" + purposeRequestedData + '\'' +
                ", extensionPreviousProposal=" + extensionPreviousProposal +
                ", originalTanNumber='" + originalTanNumber + '\'' +
                ", originalUri='" + originalUri + '\'' +
                ", originalLegalContext='" + originalLegalContext + '\'' +
                ", ftpAccount='" + ftpAccount + '\'' +
                ", requestsAreSpread=" + requestsAreSpread +
                ", estimatedNumberOfRequests=" + estimatedNumberOfRequests +
                ", feedback='" + feedback + '\'' +
                ", inspection=" + inspection +
                ", architectureVisualizationExplanation='" + architectureVisualizationExplanation + '\'' +
                ", architectureVisualization=" + architectureVisualization +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proposal proposal = (Proposal) o;
        return Objects.equals(id, proposal.id) && Objects.equals(name, proposal.name) && status == proposal.status && Objects.equals(deadline, proposal.deadline) && Objects.equals(legalDeadline, proposal.legalDeadline) && Objects.equals(tiDeadline, proposal.tiDeadline) && Objects.equals(explanationDeadline, proposal.explanationDeadline) && Objects.equals(businessContext, proposal.businessContext) && Objects.equals(legalContext, proposal.legalContext) && Objects.equals(functionalSetup, proposal.functionalSetup) && Objects.equals(technicalSetup, proposal.technicalSetup) && Objects.equals(requiresPersonalData, proposal.requiresPersonalData) && Objects.equals(purposeRequestedData, proposal.purposeRequestedData) && Objects.equals(extensionPreviousProposal, proposal.extensionPreviousProposal) && Objects.equals(originalTanNumber, proposal.originalTanNumber) && Objects.equals(originalUri, proposal.originalUri) && Objects.equals(originalLegalContext, proposal.originalLegalContext) && Objects.equals(ftpAccount, proposal.ftpAccount) && Objects.equals(requestsAreSpread, proposal.requestsAreSpread) && estimatedNumberOfRequests == proposal.estimatedNumberOfRequests && Objects.equals(feedback, proposal.feedback) && Objects.equals(inspection, proposal.inspection) && Objects.equals(architectureVisualizationExplanation, proposal.architectureVisualizationExplanation) && Objects.equals(architectureVisualization, proposal.architectureVisualization) && Objects.equals(organisation, proposal.organisation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, deadline, legalDeadline, tiDeadline, explanationDeadline, businessContext, legalContext, functionalSetup, technicalSetup, requiresPersonalData, purposeRequestedData, extensionPreviousProposal, originalTanNumber, originalUri, originalLegalContext, ftpAccount, requestsAreSpread, estimatedNumberOfRequests, feedback, inspection, architectureVisualizationExplanation, architectureVisualization, organisation);
    }
}
