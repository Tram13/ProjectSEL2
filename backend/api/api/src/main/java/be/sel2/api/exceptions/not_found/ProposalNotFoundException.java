package be.sel2.api.exceptions.not_found;

public class ProposalNotFoundException extends NotFoundException {

    public ProposalNotFoundException(Long id) {
        super("proposal", id);
    }

}
