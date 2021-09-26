package be.sel2.api.dtos;

import be.sel2.api.entities.Certificate;
import be.sel2.api.entities.FileMeta;
import be.sel2.api.entities.Proposal;
import be.sel2.api.exceptions.InvalidInputException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CertificateDTO extends DTOObject<Certificate> {

    Long proposalId;
    Long file;

    @Override
    protected List<String> getInvalidNonNullableFields() {
        List<String> list = new ArrayList<>();

        if (proposalId == null) list.add("proposalId");
        if (file == null) list.add("file");

        return list;
    }

    @Override
    public void testValidity(boolean requireNotNullFields) throws InvalidInputException {
        InvalidInputException ex = super.buildException(requireNotNullFields);

        if (ex.containsMessages()) {
            throw ex;
        }
    }

    @Override
    public Certificate getEntity() {
        Certificate cert = new Certificate();

        FileMeta f = new FileMeta();
        f.setId(file);
        cert.setFile(f);

        Proposal prop = new Proposal();
        prop.setId(proposalId);
        cert.setProposal(prop);

        return cert;
    }

    @Override
    public void updateEntity(Certificate other) {
        // Patch does not exist
    }
}
