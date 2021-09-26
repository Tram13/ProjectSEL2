package be.sel2.api.datajpa_tests.livetests;

import be.sel2.api.entities.Member;
import be.sel2.api.entities.Organisation;
import be.sel2.api.repositories.OrganisationRepository;
import be.sel2.api.repositories.MemberRepository;
import be.sel2.api.entities.UserInfo;
import be.sel2.api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
// ** IMPORTANT NOTICE **
// These tests use an actual database, not a mocked one. This means that the database must be installed on the system
// that this test is executed on.
@ActiveProfiles("testwithdatabaseconnection")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIfEnvironmentVariable(named = "LIVETESTS", matches = "1")
class MemberDataJpaLiveTests {

    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void checkRemovingOrganisationFromUser() {
        Organisation monarchy = new Organisation("Koningshuis België", "9999999990", "OVO123456", "55555", "DienstServies");
        Organisation milbe = new Organisation("Defensie", "1234567890", "OVO666666", "12345", "Staatsveiligheid");
        UserInfo philippe = new UserInfo("Philippe", "de Belgique", "fluppe@monarchie.be", "MathildeIsBae", UserInfo.Userrole.ADMIN);
        Member membership = new Member(monarchy, philippe, Member.MemberRole.MEMBER, true);
        Member membership2 = new Member(milbe, philippe, Member.MemberRole.MEMBER, true);
        organisationRepository.saveAndFlush(monarchy);
        organisationRepository.saveAndFlush(milbe);
        userRepository.saveAndFlush(philippe);
        memberRepository.save(membership);
        memberRepository.save(membership2);

        userRepository.refresh(philippe);

        assertEquals(2, philippe.getOrganisations().size());
        organisationRepository.delete(milbe);
        organisationRepository.flush();

        userRepository.refresh(philippe);

        assertEquals(1, philippe.getOrganisations().size());
        assertTrue(philippe.getOrganisations().contains(monarchy));
        assertFalse(philippe.getOrganisations().contains(milbe));
    }
}
