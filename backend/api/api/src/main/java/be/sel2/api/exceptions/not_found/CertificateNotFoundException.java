package be.sel2.api.exceptions.not_found;

public class CertificateNotFoundException extends NotFoundException {

    public CertificateNotFoundException(Long id) {
        super("certificate", id);
    }
}
