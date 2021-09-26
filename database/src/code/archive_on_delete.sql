---
--- "Verwijderde" entiteiten worden automatisch gearchiveerd in een andere tabel.
---

--- UserInfo
CREATE FUNCTION fn_move_deleted_user() RETURNS trigger AS $$
    BEGIN
       INSERT INTO UserInfoDeleted VALUES(OLD.id, OLD.firstName, OLD.lastName, OLD.email, OLD.password, OLD.role, OLD.created, DEFAULT);
       RETURN OLD;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_archive_deleted_user
BEFORE DELETE ON UserInfo 
FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_user();

---  Organisation
CREATE FUNCTION fn_move_deleted_organisation() RETURNS trigger as $$
BEGIN
    INSERT INTO OrganisationDeleted VALUES (OLD.id, OLD.organisationName, OLD.kboNumber, OLD.ovoCode, OLD.nisNumber, OLD.serviceProvider, OLD.approved, OLD.created, DEFAULT);
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_organisation
BEFORE DELETE ON Organisation
FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_organisation();

--- Member
CREATE FUNCTION fn_move_deleted_member() RETURNS trigger as $$
    BEGIN
        INSERT INTO MemberDeleted VALUES (OLD.id, OLD.organisationId, OLD.userId, OLD.role, OLD.accepted, OLD.created, DEFAULT);
        RETURN OLD;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_member
BEFORE DELETE
ON Member
FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_member();

--- Permission
CREATE FUNCTION fn_move_deleted_permission() RETURNS trigger as $$
	BEGIN
		INSERT INTO PermissionDeleted VALUES (OLD.id, OLD.name, OLD.description, OLD.code, OLD.link, OLD.proof, OLD.organisationId, DEFAULt);
		RETURN OLD;
	END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_permission
BEFORE DELETE ON Permission
FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_permission();

--- Proposal
CREATE FUNCTION fn_move_deleted_proposal() RETURNS trigger as
$$
BEGIN
    INSERT INTO ProposalDeleted
    VALUES (OLD.id, OLD.name, OLD.status, OLD.deadline, OLD.legalDeadline, OLD.tiDeadline, OLD.explanationDeadline,
            OLD.businessContext, OLD.legalContext, OLD.functionalSetup, OLD.technicalSetup, OLD.requiresPersonalData,
            OLD.purposeRequestedData, OLD.extensionPreviousProposal, OLD.originalTanNumber, OLD.originalUri,
            OLD.originalLegalContext, OLD.ftpAccount, OLD.requestsAreSpread, OLD.estimatedNumberOfRequests,
            OLD.feedback, OLD.inspection, OLD.architectureVisualizationExplanation, OLD.architectureVisualizationId,
            OLD.organisationId, OLD.authorizationPersonalDataId, OLD.ftp, OLD.externIpTest, OLD.externIpProd,
            OLD.externIpRangeTest, OLD.externIpRangeProd, OLD.sshKeyId, OLD.reuseUri, OLD.reuseLegalContext,
            OLD.reuseFtpAccount, OLD.reusePreviousProposal, OLD.cnPersonalData, OLD.cnRepertorium, OLD.cnOther,
            OLD.magdaMessages, OLD.onlineOption, OLD.cooperationAgreementId, OLD.processingAgreementId, OLD.peaks,
            OLD.created, DEFAULT);
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_proposal
BEFORE DELETE ON Proposal
FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_proposal();

--- Contact
CREATE FUNCTION fn_move_deleted_contact() RETURNS trigger as $$
	BEGIN
		INSERT INTO ContactDeleted VALUES (OLD.id, OLD.firstName, OLD.lastName, OLD.email, OLD.phoneNumber, OLD.created, DEFAULT, OLD.organisationId);
		RETURN OLD;
	END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_contact
BEFORE DELETE ON Contact
FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_contact();

--- ContactPropososal
CREATE FUNCTION fn_move_deleted_contact_proposal() RETURNS trigger as $$
	BEGIN
		INSERT INTO ContactProposalDeleted VALUES (OLD.contactId, OLD.proposalId, OLD.role, DEFAULT);
		RETURN OLD;
	END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_contact_proposal
BEFORE DELETE ON ContactProposal
FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_contact_proposal();

--- Service
CREATE FUNCTION fn_move_deleted_service() RETURNS trigger as $$
	BEGIN
		INSERT INTO ServiceDeleted VALUES (OLD.id, OLD.name, OLD.domain, OLD.description, OLD.needsPermissions, OLD.deprecated, OLD.created, DEFAULT);
		RETURN OLD;
	END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_service
BEFORE DELETE ON Service
FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_service();

--- ProposalService
CREATE FUNCTION fn_move_deleted_proposal_service() RETURNS trigger as $$
	BEGIN
		INSERT INTO ProposalServiceDeleted VALUES (OLD.proposalId, OLD.serviceId, OLD.deliveryMethod, old.source, DEFAULT);
		RETURN OLD;
	END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_proposal_service
BEFORE DELETE ON ProposalService
FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_proposal_service();

--- Package
CREATE FUNCTION fn_move_deleted_package() RETURNS trigger as $$
	BEGIN
		INSERT INTO PackageDeleted VALUES (OLD.id, OLD.name, OLD.deprecated, OLD.created, DEFAULT);
		RETURN OLD;
	END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_package
BEFORE DELETE ON Package
FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_package();

--- PackageService
CREATE FUNCTION fn_move_deleted_package_service() RETURNS trigger as $$
	BEGIN
		INSERT INTO PackageServiceDeleted VALUES (OLD.packageId, OLD.serviceId, OLD.source, OLD.deliveryMethod, DEFAULT);
		RETURN OLD;
	END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_package_service
BEFORE DELETE ON PackageService
FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_package_service();

--- PackageProposal
CREATE FUNCTION fn_move_deleted_package_proposal() RETURNS trigger as $$
    BEGIN
	    INSERT INTO PackageProposalDeleted VALUES (OLD.packageId, OLD.proposalId, DEFAULT);
		RETURN OLD;
	END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_package_proposal
BEFORE DELETE ON PackageProposal
FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_package_proposal();

--- ServiceDeliveryMethod
CREATE FUNCTION fn_move_deleted_service_deliverymethod() RETURNS trigger as $$
    BEGIN
        INSERT INTO ServiceDeliveryMethodDeleted VALUES (OLD.serviceId, OLD.deliveryMethod, DEFAULT);
        RETURN OLD;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_service_deliverymethod
BEFORE DELETE ON ServiceDeliveryMethod
FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_service_deliverymethod();

--- ServiceSource
CREATE FUNCTION fn_move_deleted_service_source() RETURNS trigger as $$
BEGIN
    INSERT INTO ServiceSourceDeleted VALUES (OLD.serviceId, OLD.source, DEFAULT);
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_service_source
    BEFORE DELETE ON ServiceSource
    FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_service_source();

--- Certificate
CREATE FUNCTION fn_move_deleted_certificate() RETURNS trigger as $$
BEGIN
    INSERT INTO CertificateDeleted VALUES (OLD.id, OLD.created, DEFAULT, OLD.fileId, OLD.proposalId);
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_certificate
    BEFORE DELETE ON Certificate
    FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_certificate();

--- File
CREATE FUNCTION fn_move_deleted_file() RETURNS trigger as $$
BEGIN
    INSERT INTO FileMetaDeleted VALUES (OLD.id, OLD.fileLocation, OLD.created, DEFAULT);
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_move_deleted_file
    BEFORE DELETE ON FileMeta
    FOR EACH ROW
EXECUTE PROCEDURE fn_move_deleted_file();
