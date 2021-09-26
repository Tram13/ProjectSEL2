package be.sel2.api.util_tests;

import be.sel2.api.entities.*;
import be.sel2.api.entities.relations.ContactProposal;
import be.sel2.api.users.UserInfoDetails;
import be.sel2.api.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilsTest {

    @ParameterizedTest
    @MethodSource("provideContextWithRole")
    void hasRoleShouldWorkEveryTime(SecurityContext context, String expectedRole){
        String[] possibleRoles = {"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_EMPLOYEE"};

        String role = null;
        for (String testedRole : possibleRoles){
            if(SecurityUtils.hasRole(context, testedRole)){
                role = testedRole;
            }
        }
        assertEquals(expectedRole, role);
    }

    private static Stream<Arguments> provideContextWithRole() {
        return Stream.of(
                Arguments.of(new MockedSecurityContext("ROLE_ADMIN", true), "ROLE_ADMIN"),
                Arguments.of(new MockedSecurityContext("ROLE_CUSTOMER", true), "ROLE_CUSTOMER"),
                Arguments.of(new MockedSecurityContext("ROLE_EMPLOYEE", true), "ROLE_EMPLOYEE"),
                Arguments.of(new MockedSecurityContext("ROLE_ADMIN", false), null)
        );
    }

    @Test
    void testIsMember() {
        SecurityContext context = new MockedSecurityContext("ROLE_CUSTOMER", true);
        Organisation org = new Organisation();
        org.setMembers(new HashSet<>());

        assertFalse(SecurityUtils.isMember(context, false, org), "Should be false for empty memberList");
        assertFalse(SecurityUtils.isMember(context, true, org), "Should be false for empty memberList");

        Member member = new Member();
        UserInfo user = new UserInfo();
        user.setId(5L);
        member.setUser(user);
        member.setOrganisation(org);
        member.setRole(Member.MemberRole.MEMBER);
        member.setAccepted(true);
        org.getMembers().add(member);

        assertFalse(SecurityUtils.isMember(context, false, org), "Should be false when user is not on the list");
        assertFalse(SecurityUtils.isMember(context, true, org), "Should be false when user is not on the list");

        user.setId(1L);

        assertTrue(SecurityUtils.isMember(context, false, org), "Should be true when user is the list");
        assertFalse(SecurityUtils.isMember(context, true, org), "Should be false when user is on the list, but no manager");

        Member member2 = new Member();
        UserInfo user2 = new UserInfo();
        user2.setId(5L);
        member2.setUser(user);
        member2.setOrganisation(org);
        member2.setRole(Member.MemberRole.MEMBER);
        member2.setAccepted(true);
        org.getMembers().add(member2);

        user.setId(25L);
        member.setRole(Member.MemberRole.MANAGER);

        assertFalse(SecurityUtils.isMember(context, false, org), "Should be false when user is not on the list");
        assertFalse(SecurityUtils.isMember(context, true, org), "Should be false when user is not on the list");

        user.setId(1L);

        assertTrue(SecurityUtils.isMember(context, false, org), "Should be true when user is on the list with others");
        assertTrue(SecurityUtils.isMember(context, true, org), "Should be true when user is manager");
    }

    @Test
    void testIsContact () {
        SecurityContext adminContext = new MockedSecurityContext("ROLE_ADMIN", true);
        assertTrue(SecurityUtils.isContact(adminContext, false, new Proposal()),
                "Does not need to be checked in case of ADMIN");

        SecurityContext employeeContext = new MockedSecurityContext("ROLE_EMPLOYEE", true);
        assertTrue(SecurityUtils.isContact(employeeContext, false, new Proposal()),
                "Does not need to be checked in case of EMPLOYEE");

        SecurityContext context = new MockedSecurityContext("ROLE_CUSTOMER", true);

        Proposal prop = new Proposal();
        prop.setContacts(new HashSet<>());

        assertFalse(SecurityUtils.isContact(context, false, prop),
                "False when contactList is empty");

        ContactProposal conProp1 = new ContactProposal();
        Contact contact1 = new Contact();
        contact1.setEmail("ambethie@artevelde3.be");
        conProp1.setContact(contact1);
        conProp1.setProposal(prop);
        conProp1.setRole(ContactProposal.Contactrole.BUSINESS);

        prop.getContacts().add(conProp1);

        assertTrue(SecurityUtils.isContact(context, false, prop),
                "True when on the list");

        assertFalse(SecurityUtils.isContact(context, true, prop),
                "False when on the list but is not submitter");

        conProp1.setRole(ContactProposal.Contactrole.SUBMITTER);

        assertTrue(SecurityUtils.isContact(context, false, prop),
                "True when on the list");

        assertTrue(SecurityUtils.isContact(context, true, prop),
                "True when on list as submitter");

    }



    private static class MockedSecurityContext implements SecurityContext {

        private final String role;
        private final boolean authenticated;

        public MockedSecurityContext(String role, boolean authenticated) {
            this.role = role;
            this.authenticated = authenticated;
        }

        @Override
        public Authentication getAuthentication() {
            return new Authentication() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return List.of(new SimpleGrantedAuthority(role));
                }

                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getDetails() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    return new UserInfoDetails(1L, "ambethie@artevelde3.be", "Hunter21",
                            List.of(new SimpleGrantedAuthority(role)));
                }

                @Override
                public boolean isAuthenticated() {
                    return authenticated;
                }

                @Override
                public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                    // No need to do anything in this mocked version
                }

                @Override
                public String getName() {
                    return "ambethie@artevelde3.be";
                }
            };
        }

        @Override
        public void setAuthentication(Authentication authentication) {
            // No need to implement anything in this mocked version
        }
    }
}
