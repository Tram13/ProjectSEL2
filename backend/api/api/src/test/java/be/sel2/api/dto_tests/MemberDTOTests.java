package be.sel2.api.dto_tests;

import be.sel2.api.dtos.MemberDTO;
import be.sel2.api.entities.Member;
import be.sel2.api.entities.UserInfo;
import be.sel2.api.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MemberDTOTests {

    @Test
    void testNullableList() {
        MemberDTO dto = new MemberDTO();

        assertThrows(InvalidInputException.class, () -> dto.testValidity(true));

        List<String> nullCheckedFields = List.of();

        try {
            dto.testValidity(true);
        } catch (InvalidInputException ex) {
            assertTrue(ex.containsMessages());
            nullCheckedFields = ex.getErrors().stream()
                    .map(InvalidInputException.ParamErrorPair::getParameter)
                    .collect(Collectors.toList());
        }

        assertTrue(nullCheckedFields.containsAll(List.of("role", "userId|email")));

        dto.setRole("member");
        dto.setEmail("testMail@ugent.be");

        assertDoesNotThrow(() -> dto.testValidity(true));

        dto.setUserId(1L);

        assertThrows(InvalidInputException.class, () -> dto.testValidity(false));

        try {
            dto.testValidity(false);
        } catch (InvalidInputException ex) {
            assertTrue(ex.containsMessages());
            nullCheckedFields = ex.getErrors().stream()
                    .filter(pair -> pair.getMessage().equals("cannot give both, choose one"))
                    .map(InvalidInputException.ParamErrorPair::getParameter)
                    .collect(Collectors.toList());
        }

        assertEquals(1, nullCheckedFields.size());
        assertEquals("userId|email", nullCheckedFields.get(0));
    }

    @Test
    void createdEntityStillNeedsToBeAccepted() {
        MemberDTO dto = new MemberDTO();

        dto.setAccepted(true);

        Member member = dto.getEntity();

        assertFalse(member.getAccepted());
    }

    @Test
    void updatingOnlyAltersRoleAndAccepted() {
        MemberDTO dto = new MemberDTO();
        Member member = new Member();
        member.setAccepted(false);
        member.setRole(Member.MemberRole.MEMBER);
        UserInfo user = new UserInfo();
        user.setId(1L);
        user.setFirstName("Tester");
        member.setUser(user);

        dto.setAccepted(true);
        dto.setRole("manager");
        dto.setUserId(25L);

        dto.updateEntity(member);

        assertTrue(member.getAccepted());
        assertEquals(Member.MemberRole.MANAGER, member.getRole());
        assertEquals(1L, member.getUser().getId());
    }

    @Test
    void patchShouldNeverSetAcceptedToFalse() {
        MemberDTO dto = new MemberDTO();
        Member member = new Member();
        member.setAccepted(false);

        dto.setAccepted(true);

        dto.updateEntity(member);

        assertTrue(member.getAccepted());

        dto.setAccepted(false);
        dto.updateEntity(member);

        assertTrue(member.getAccepted());
    }
}
