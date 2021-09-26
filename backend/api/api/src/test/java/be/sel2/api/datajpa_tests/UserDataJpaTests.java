package be.sel2.api.datajpa_tests;

import be.sel2.api.entities.Member;
import be.sel2.api.entities.Organisation;
import be.sel2.api.entities.UserInfo;
import be.sel2.api.repositories.MemberRepository;
import be.sel2.api.repositories.OrganisationRepository;
import be.sel2.api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // Set the active profile to use application-test.properties
@DataJpaTest
class UserDataJpaTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private MemberRepository memberRepository;

    private UserInfo getPhilippe() {
        return new UserInfo(
                "Philippe",
                "de Belgique",
                "fluppe@monarchie.be",
                "MathildeIsBae",
                UserInfo.Userrole.ADMIN);
    }

    @Test
    void checkUserDataEquals() {
        UserInfo philippe = getPhilippe();
        userRepository.save(philippe);

        UserInfo otherUser = new UserInfo("Jean-Claude", "Van Damme", "jcvd@gmail.com", "JCVDInDaHouse", UserInfo.Userrole.ADMIN);
        userRepository.save(otherUser);
        assertNotEquals(philippe, otherUser);

        UserInfo otherUser2 = getPhilippe();
        assertNotEquals(philippe, otherUser2); // IDs verschillen
        otherUser2.setId(philippe.getId());
        assertEquals(philippe, otherUser2);

        UserInfo otherUser3 = getPhilippe();
        otherUser3.setFirstName("Mathilde");
        otherUser3.setId(philippe.getId());
        assertNotEquals(philippe, otherUser3);

        otherUser3 = getPhilippe();
        otherUser3.setLastName("d'Udekem d'Acoz");
        otherUser3.setId(philippe.getId());
        assertNotEquals(philippe, otherUser3);

        otherUser3 = getPhilippe();
        otherUser3.setEmail("mathilde@monarchie.be");
        otherUser3.setId(philippe.getId());
        assertNotEquals(philippe, otherUser3);

        otherUser3 = getPhilippe();
        otherUser3.setPassword("gjzhbgjkbzeiugzheJHFJHKJ4578");
        otherUser3.setId(philippe.getId());
        assertNotEquals(philippe, otherUser3);

        otherUser3 = getPhilippe();
        otherUser3.setRole(UserInfo.Userrole.CUSTOMER);
        otherUser3.setId(philippe.getId());
        assertNotEquals(philippe, otherUser3);

        otherUser3 = getPhilippe();
        otherUser3.setRole(UserInfo.Userrole.EMPLOYEE);
        otherUser3.setId(philippe.getId());
        assertNotEquals(philippe, otherUser3);
    }

    @Test
    void checkSaveNewToUserRepository() {
        UserInfo philippe = new UserInfo("Philippe", "de Belgique", "fluppe@monarchie.be", "MathildeIsBae", UserInfo.Userrole.ADMIN);
        userRepository.save(philippe);
        UserInfo copyPhilippe = userRepository.getOne(philippe.getId());
        assertNotNull(copyPhilippe);
        assertEquals(philippe, copyPhilippe);
    }

    @Test
    void checkSaveNewToUserRepositoryWithOrganisation() {
        Organisation monarchy = new Organisation("Koningshuis België", "KoningKBO", "KoningOVO", "KoningNIS", "DienstServies");
        UserInfo philippe = new UserInfo("Philippe", "de Belgique", "fluppe@monarchie.be", "MathildeIsBae", UserInfo.Userrole.ADMIN);
        Member memberShip = new Member(monarchy, philippe, Member.MemberRole.MEMBER, true);

        organisationRepository.save(monarchy);
        userRepository.save(philippe);
        memberRepository.saveAndFlush(memberShip);
        userRepository.refresh(philippe);

        UserInfo copyPhilippe = userRepository.getOne(philippe.getId());
        assertNotNull(copyPhilippe);
        assertEquals(copyPhilippe.getOrganisations(), Set.of(monarchy));
        assertEquals(philippe, copyPhilippe);
    }

    @Test
    void checkSaveUpdateToUserRepositoryIsNotEqualToOldValue() {
        UserInfo philippe1 = new UserInfo("Philippe", "de Belgique", "fluppe@monarchie.be", "MathildeIsBae", UserInfo.Userrole.ADMIN);
        UserInfo philippe2 = new UserInfo("Philippe", "de Belgique", "fluppe@monarchie.be", "MathildeIsBae", UserInfo.Userrole.ADMIN);
        assertNull(philippe1.getId());
        assertNull(philippe2.getId());
        assertEquals(philippe1.getFirstName(), philippe2.getFirstName());
        userRepository.save(philippe1);
        userRepository.save(philippe2);
        assertNotEquals(philippe1, philippe2); // ID is anders
        assertEquals(philippe1.getFirstName(), philippe2.getFirstName()); // De naam blijft wel gelijk

        philippe1.setFirstName("Mathilde");
        userRepository.save(philippe1);
        UserInfo philippe1FromDatabase = userRepository.getOne(philippe1.getId());
        assertEquals(philippe1.getId(), philippe1FromDatabase.getId());
        assertEquals(philippe1FromDatabase.getLastName(), philippe2.getLastName());
        assertNotEquals(philippe1FromDatabase.getFirstName(), philippe2.getFirstName());
    }

    @Test
    void checkSaveUpdateToUserRepositoryDoesUpdate() {
        UserInfo philippe = new UserInfo("Philippe", "de Belgique", "fluppe@monarchie.be", "MathildeIsBae", UserInfo.Userrole.ADMIN);
        userRepository.save(philippe);

        UserInfo philippeCopy = userRepository.getOne(philippe.getId());
        assertEquals("Philippe", philippeCopy.getFirstName());
        philippeCopy.setFirstName("Mathilde");
        userRepository.save(philippeCopy);

        UserInfo mathilde = userRepository.getOne(philippeCopy.getId());
        assertEquals("Mathilde", mathilde.getFirstName());
    }

    @Test
    void checkDeleteFromUserRepositoryById() {
        UserInfo philippe = new UserInfo("Philippe", "de Belgique", "fluppe@monarchie.be", "MathildeIsBae", UserInfo.Userrole.ADMIN);
        userRepository.save(philippe);
        UserInfo philippeCopy = userRepository.getOne(philippe.getId());
        assertNotNull(philippeCopy); // Controle dat het is opgeslagen
        Long philippeId = philippe.getId();
        userRepository.deleteById(philippe.getId());
        assertThrows(JpaObjectRetrievalFailureException.class, () -> userRepository.getOne(philippeId));
    }

    @Test
    void checkDeleteFromUserRepositoryByObject() {
        UserInfo philippe = new UserInfo("Philippe", "de Belgique", "fluppe@monarchie.be", "MathildeIsBae", UserInfo.Userrole.ADMIN);
        userRepository.save(philippe);
        UserInfo philippeCopy = userRepository.getOne(philippe.getId());
        assertNotNull(philippeCopy); // Controle dat het is opgeslagen
        Long philippeId = philippe.getId();
        userRepository.delete(philippe);
        assertEquals("Philippe", philippeCopy.getFirstName()); // Controle dat dit het juiste element is in de databank
        assertThrows(JpaObjectRetrievalFailureException.class, () -> userRepository.getOne(philippeId));
    }

    @Test
    void checkEnumConversion() {
        assertEquals(UserInfo.Userrole.CUSTOMER, UserInfo.Userrole.fromString("customer"));
        assertEquals(UserInfo.Userrole.ADMIN, UserInfo.Userrole.fromString("admin"));
        assertEquals(UserInfo.Userrole.EMPLOYEE, UserInfo.Userrole.fromString("employee"));
        assertNull(UserInfo.Userrole.fromString("EMPLOYEE"));
        assertNull(UserInfo.Userrole.fromString("employe"));
        assertNull(UserInfo.Userrole.fromString("administrator"));
        assertNull(UserInfo.Userrole.fromString(""));
        assertNull(UserInfo.Userrole.fromString(null));
    }
}