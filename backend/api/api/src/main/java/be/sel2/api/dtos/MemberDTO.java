package be.sel2.api.dtos;

import be.sel2.api.entities.Member;
import be.sel2.api.entities.UserInfo;
import be.sel2.api.exceptions.InvalidInputException;
import be.sel2.api.util.InputValidator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class MemberDTO extends DTOObject<Member> {

    private static List<String> roleOptions = Arrays.stream(Member.MemberRole.values())
            .map(memberRole -> memberRole.name().toLowerCase())
            .collect(Collectors.toList());


    private String role;
    private String email;
    private Long userId;
    private Boolean accepted;

    @Override
    protected List<String> getInvalidNonNullableFields() {
        List<String> res = new ArrayList<>();

        if (role == null) {
            res.add("role");
        }

        return res;
    }

    @Override
    public void testValidity(boolean requireNotNullFields) throws InvalidInputException {
        InvalidInputException exceptionList = super.buildException(requireNotNullFields);

        if (requireNotNullFields && email == null && userId == null) {
            exceptionList.addMessage("userId|email", "at least one may not be null");
        }

        if (email != null && userId != null) {
            exceptionList.addMessage("userId|email", "cannot give both, choose one");
        }

        validateField(role, "role",
                roleOptions::contains,
                InputValidator.listOfOptionsRule(roleOptions),
                exceptionList);

        if (exceptionList.containsMessages()) {
            throw exceptionList;
        }
    }

    @Override
    public Member getEntity() {
        Member res = new Member();

        res.setRole(getEnumRole());
        res.setAccepted(false);

        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        userInfo.setEmail(email);

        res.setUser(userInfo);

        return res;
    }

    @Override
    public void updateEntity(Member other) {
        if (role != null) other.setRole(getEnumRole());
        // once accepted this field cant be false
        if (accepted != null) other.setAccepted(other.getAccepted() || accepted);
    }

    public void setRole(String role) {
        if (role == null) {
            this.role = null;
        } else {
            this.role = role.toLowerCase();
        }
    }

    private Member.MemberRole getEnumRole() {
        return Member.MemberRole.fromString(this.role);
    }
}
