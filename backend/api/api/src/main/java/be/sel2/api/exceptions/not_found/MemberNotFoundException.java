package be.sel2.api.exceptions.not_found;

public class MemberNotFoundException extends NotFoundException { // Custom error

    public MemberNotFoundException(Long id) {
        super("member", id);
    }

    public MemberNotFoundException(Long userId, Long orgId) {
        super("member", userId, orgId);
    }

}
