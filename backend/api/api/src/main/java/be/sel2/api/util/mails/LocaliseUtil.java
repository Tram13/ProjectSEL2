package be.sel2.api.util.mails;

import be.sel2.api.entities.Proposal;

import java.util.Map;

/**
 * A utility class used to translate variables to user friendly information.
 * Used to write mails.
 */
public class LocaliseUtil {

    private LocaliseUtil() {
    }

    private static final Map<Proposal.ProposalStatus, String> proposalStatusTranslation = Map.of(
            Proposal.ProposalStatus.DRAFT, "Ontwerp",
            Proposal.ProposalStatus.COMPLETED, "Ingediend",
            Proposal.ProposalStatus.IN_REVIEW, "In review",
            Proposal.ProposalStatus.PENDING_FEEDBACK, "Wachtend op feedback",
            Proposal.ProposalStatus.ACCEPTED, "Goedgekeurd",
            Proposal.ProposalStatus.DENIED, "Afgewezen",
            Proposal.ProposalStatus.CANCELLED, "Ingetrokken"
    );

    public static String localize(Proposal.ProposalStatus value) {
        return proposalStatusTranslation.get(value);
    }
}
