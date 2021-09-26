package be.sel2.api.datajpa_tests;

import be.sel2.api.entities.Member;
import be.sel2.api.entities.Organisation;
import be.sel2.api.repositories.OrganisationRepository;
import be.sel2.api.repositories.MemberRepository;
import be.sel2.api.entities.UserInfo;
import be.sel2.api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // Set the active profile to use application-test.properties
@DataJpaTest
class MemberDataJpaTests {

    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void checkUpdateOfForeignKeyMappingFail() {
        // When an organisation updates its primary key, that change is propagated to all relevant users
        Organisation monarchy = new Organisation("Koningshuis België", "1111111111", "OVO123456", "54321", "DienstServies");

        UserInfo philippe = new UserInfo("Philippe", "de Belgique", "fluppe@monarchie.be", "MathildeIsBae", UserInfo.Userrole.ADMIN);

        Member memberShip = new Member(monarchy, philippe, Member.MemberRole.MEMBER, true);
        organisationRepository.save(monarchy);
        userRepository.save(philippe);
        memberRepository.save(memberShip);

        // Update ID of organisation
        long oldId = monarchy.getId();
        long newId = oldId + 5;  // Adding 5 to random generated ID
        monarchy.setId(newId);
        organisationRepository.save(monarchy);

        userRepository.refresh(philippe);

        // Check if foreign key organisationId is updated for the user
        Set<Organisation> royalOrgs = philippe.getOrganisations();
        Organisation copyMonarchy = royalOrgs.iterator().next();
        assertNotEquals(oldId, copyMonarchy.getId());
    }

    @Test
    void checkUpdateOfForeignKeyMapping() {
        // When an organisation updates its primary key, that change is propagated to all relevant users
        Organisation monarchy = new Organisation("Koningshuis België", "1111111111", "OVO123456", "12345", "DienstServies");

        UserInfo philippe = new UserInfo("Philippe", "de Belgique", "fluppe@monarchie.be", "MathildeIsBae", UserInfo.Userrole.ADMIN);

        Member memberShip = new Member(monarchy, philippe, Member.MemberRole.MANAGER, true);
        organisationRepository.save(monarchy);
        userRepository.save(philippe);
        memberRepository.save(memberShip);

        // Update ID of organisation
        long newId = monarchy.getId() + 5;  // Adding 5 to random generated ID
        monarchy.setId(newId);
        organisationRepository.save(monarchy);

        userRepository.refresh(philippe);

        // Check if foreign key organisationId is updated for the user
        Set<Organisation> royalOrgs = philippe.getOrganisations();
        Organisation copyMonarchy = royalOrgs.iterator().next();
        assertEquals(monarchy.getId(), copyMonarchy.getId());
    }

    @Test
    void checkAddingOrganisationToUser() {
        Organisation monarchy = new Organisation("Koningshuis België", "999999999", "OVO123456", "55555", "DienstServies");
        Organisation milbe = new Organisation("Defensie", "123456789", "OVO666666", "12345", "Staatsveiligheid");
        UserInfo philippe = new UserInfo("Philippe", "de Belgique", "fluppe@monarchie.be", "MathildeIsBae", UserInfo.Userrole.ADMIN);

        Member membership = new Member(monarchy, philippe, Member.MemberRole.MEMBER, true);

        organisationRepository.save(monarchy);
        organisationRepository.save(milbe);
        userRepository.save(philippe);
        memberRepository.save(membership);

        userRepository.refresh(philippe);

        assertTrue(philippe.getOrganisations().stream().findFirst().isPresent()); // Heeft minstens 1 organisatie
        assertEquals(1, philippe.getOrganisations().size()); // Heeft exact 1 organisatie
        assertEquals("Koningshuis België", philippe.getOrganisations().stream().findFirst().get().getOrganisationName()); // Heeft de juiste organisatie

        Member membership2 = new Member(milbe, philippe, Member.MemberRole.MEMBER, true);
        memberRepository.save(membership2);

        userRepository.refresh(philippe);

        assertTrue(philippe.getOrganisations().stream().findFirst().isPresent()); // Heeft minstens 1 organisatie
        assertEquals(2, philippe.getOrganisations().size()); // Heeft exact 2 organisaties
        assertEquals(1, philippe.getOrganisations().stream().filter(org -> org.getOrganisationName().equals("Koningshuis België")).toArray().length); // Heeft Koningshuis als organisatie
        assertEquals(1, philippe.getOrganisations().stream().filter(org -> org.getOrganisationName().equals("Defensie")).toArray().length); // Heeft Defensie als organisatie
    }

    @Test
    void addUserToNonExistingOrganisationFail() {
        UserInfo philippe = new UserInfo("Philippe", "de Belgique", "fluppe@monarchie.be", "MathildeIsBae", UserInfo.Userrole.ADMIN);
        Organisation invalidOrganisation = new Organisation();
        userRepository.saveAndFlush(philippe);
        Member membership = new Member(invalidOrganisation, philippe, Member.MemberRole.MEMBER, true);

        assertThrows(InvalidDataAccessApiUsageException.class, () -> memberRepository.saveAndFlush(membership));
    }
}
