package be.sel2.api.util;

import be.sel2.api.entities.Member;
import be.sel2.api.entities.Organisation;
import be.sel2.api.entities.Proposal;
import be.sel2.api.entities.relations.ContactProposal;
import be.sel2.api.users.UserInfoDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

import java.util.stream.Stream;

/**
 * Utils to easily find data in Security context
 */
public class SecurityUtils {

    private SecurityUtils() {
    } // Prevent instantiation

    public static boolean hasRole(SecurityContext context, String role) {
        if (context == null) {
            return false;
        }
        if (!context.getAuthentication().isAuthenticated()) {
            return false;
        }
        return context.getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority(role));
    }

    public static UserInfoDetails getDetails(SecurityContext context) {
        return (UserInfoDetails) context.getAuthentication().getPrincipal();
    }

    private static boolean userIsMember(boolean requiresManager, Long userId, Organisation org) {
        Stream<Member> stream = org.getMembers().stream();
        if (requiresManager) {
            return stream.anyMatch(member ->
                    member.getUser().getId().equals(userId)
                            && member.getAccepted()
                            && member.getRole().equals(Member.MemberRole.MANAGER)
            );
        }
        return stream.anyMatch(member ->
                member.getUser()
                        .getId().equals(userId));
    }

    /**
     * Checks that any CUSTOMER calling the method is also a MEMBER
     *
     * @param context        the current security context
     * @param requireManager if true, requires the user to be a MANAGER
     * @param org            the organisation for which one needs to be a MEMBER
     * @return a boolean that indicates whether or not the requirements were met
     */
    public static boolean isMember(SecurityContext context, boolean requireManager, Organisation org) {

        if (hasRole(context, "ROLE_CUSTOMER")) {
            //user is customer
            Long userId = getDetails(context).getId();
            return userIsMember(requireManager, userId, org);
        }
        return true;
    }

    private static boolean userIsContact(boolean requiresSubmitter, String email, Proposal prop) {
        Stream<ContactProposal> stream = prop.getContacts().stream();
        if (requiresSubmitter) {
            return stream.anyMatch(contactProposal ->
                    contactProposal.getContact()
                            .getEmail().equals(email)
                            &&
                            contactProposal.getRole()
                                    .equals(ContactProposal.Contactrole.SUBMITTER)
            );
        }
        return stream.anyMatch(contactProposal ->
                contactProposal.getContact()
                        .getEmail().equals(email));
    }

    /**
     * Checks that any CUSTOMER calling the method is also a CONTACT
     *
     * @param context          the current security context
     * @param requireSubmitter if true, requires the user to be a SUBMITTER
     * @param prop             the proposal for which one needs to be a CONTACT
     * @return a boolean that indicates whether or not the requirements were met
     */
    public static boolean isContact(SecurityContext context, boolean requireSubmitter, Proposal prop) {

        if (hasRole(context, "ROLE_CUSTOMER")) {
            //user is customer
            String email = getDetails(context).getUsername();
            return userIsContact(requireSubmitter, email, prop);
        }
        return true;
    }

    public static boolean isContactOrMember(SecurityContext context, boolean requireManager, boolean requireSubmitter, Proposal prop) {
        return isContact(context, requireSubmitter, prop) || isMember(context, requireManager, prop.getOrganisation());
    }
}
