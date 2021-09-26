-- Deze code reset de databank, alle gegevens worden verwijderd!
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO admin;
GRANT ALL ON SCHEMA public TO public;


--
-- TABLE: Organisation
-- 
--  

CREATE TABLE Organisation (
  id bigserial NOT NULL ,
  organisationName character varying (1792) NOT NULL ,
  kboNumber character varying (10) NOT NULL ,
  ovoCode character varying (9),
  nisNumber character varying (5),
  serviceProvider character varying (1792) NOT NULL ,
  approved Boolean NOT NULL  DEFAULT FALSE,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE Organisation ADD CONSTRAINT organisation_primary_key PRIMARY KEY (id);

-- 
ALTER TABLE Organisation ADD CONSTRAINT uniqueOrganisationConstraint UNIQUE (kboNumber,ovoCode,nisNumber);

--
-- TABLE: UserInfo
-- PostGreSQL laat de tabelnaam "User" niet toe. Daarom deze aangepaste naamgeving.
-- 
-- De lengte (1792) is gekozen door de langste encoded HTML entity (7) te nemen en
-- die dan te vermenigvuldigen met het maximaal aantal tekens (256).
--  

CREATE TABLE UserInfo (
  id bigserial NOT NULL ,
  firstName character varying (1792) NOT NULL ,
  lastName character varying (1792) NOT NULL ,
  email character varying (1792) NOT NULL , -- Bij email wordt er een simpele regex toegevoegd die controleert of het emailadres geldig is.
  password character varying (1792) NOT NULL ,
  role character varying (20) NOT NULL ,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE UserInfo ADD CONSTRAINT user_primary_key PRIMARY KEY (id);

-- 
ALTER TABLE UserInfo ADD CONSTRAINT email_unique_constraint UNIQUE (email);

--
-- TABLE: Member
-- 
--  

CREATE TABLE Member (
  id bigserial NOT NULL ,
  organisationId bigserial NOT NULL ,
  userId bigserial NOT NULL ,
  role character varying (20) NOT NULL ,
  accepted boolean NOT NULL  DEFAULT FALSE,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE Member ADD CONSTRAINT member_primary_key PRIMARY KEY (id);

-- 
ALTER TABLE Member ADD CONSTRAINT foreignUser FOREIGN KEY (userId) REFERENCES UserInfo(id) ON UPDATE CASCADE ON DELETE CASCADE;

-- 
ALTER TABLE Member ADD CONSTRAINT foreignOrganisation FOREIGN KEY (organisationId) REFERENCES Organisation(id) ON UPDATE CASCADE ON DELETE CASCADE;

--
-- TABLE: FileMeta
-- 
--  

CREATE TABLE FileMeta (
  id bigserial NOT NULL ,
  fileLocation character varying (1792) NOT NULL ,
  fileSize bigint NOT NULL ,
  created timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE FileMeta ADD CONSTRAINT file_primarykey PRIMARY KEY (id);

--
-- TABLE: Permission
-- "Machtiging"
-- 
-- 28672 tekens voor de beschrijving komt overeen met maximale lengte van een
-- encoded HTML-entity (7) vermenigvuldigd met maximaal aantal invoertekens (4096).
--  

CREATE TABLE Permission (
  id bigserial NOT NULL ,
  name character varying (1792) NOT NULL ,
  description character varying (28672) NOT NULL ,
  code character varying (1792) NOT NULL ,
  link character varying (1792) NOT NULL ,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW(),
  proof bigint NOT NULL ,
  organisationId bigint NOT NULL 
);

-- 
ALTER TABLE Permission ADD CONSTRAINT permission_primary_key PRIMARY KEY (id);

-- 
ALTER TABLE Permission ADD CONSTRAINT foreignOrganisation FOREIGN KEY (organisationId) REFERENCES Organisation(id) ON UPDATE CASCADE ON DELETE CASCADE;

-- 
ALTER TABLE Permission ADD CONSTRAINT foreignFile FOREIGN KEY (proof) REFERENCES FileMeta(id) ON UPDATE CASCADE ON DELETE CASCADE;

--
-- TABLE: Proposal
-- 
--  

CREATE TABLE Proposal (
  id bigserial NOT NULL ,
  name character varying (1792) NOT NULL ,
  status character varying (20) NOT NULL  DEFAULT 'draft',
  deadline timestamp with time zone,
  legalDeadline timestamp with time zone,
  tiDeadline timestamp with time zone,
  explanationDeadline character varying (28672),
  businessContext character varying (28672),
  legalContext character varying (28672),
  functionalSetup character varying (28672),
  technicalSetup character varying (28672),
  requiresPersonalData character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  purposeRequestedData character varying (28672),
  extensionPreviousProposal character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  originalTanNumber character varying (1792),
  originalUri character varying (1792),
  originalLegalContext character varying (28672),
  ftpAccount character varying (1792),
  requestsAreSpread character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  estimatedNumberOfRequests character varying (20),
  feedback character varying (114688),
  inspection character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  architectureVisualizationExplanation character varying (28672),
  architectureVisualizationId bigint,
  organisationId bigserial NOT NULL ,
  authorizationPersonalDataId bigint,
  ftp character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  externIpTest character varying (1792),
  externIpProd character varying (1792),
  externIpRangeTest character varying (1792),
  externIpRangeProd character varying (1792),
  sshKeyId bigint,
  reuseUri character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  reuseLegalContext character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  reuseFtpAccount character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  reusePreviousProposal character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  cnPersonalData character varying (1792),
  cnRepertorium character varying (1792),
  cnOther character varying (1792),
  magdaMessages character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  onlineOption character varying (30),
  cooperationAgreementId bigint,
  processingAgreementId bigint,
  peaks bigint,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE Proposal ADD CONSTRAINT proposal_primary_key PRIMARY KEY (id);

-- 
ALTER TABLE Proposal ADD CONSTRAINT foreignOrganisation FOREIGN KEY (organisationId) REFERENCES Organisation(id) ON UPDATE CASCADE ON DELETE CASCADE;

-- 
ALTER TABLE Proposal ADD CONSTRAINT foreignFileMeta FOREIGN KEY (architectureVisualizationId) REFERENCES FileMeta(id) ON UPDATE CASCADE ON DELETE CASCADE;

--
-- TABLE: Certificate
-- 
--  

CREATE TABLE Certificate (
  id bigserial NOT NULL ,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL ,
  fileId bigint NOT NULL ,
  proposalId bigint NOT NULL 
);

-- 
ALTER TABLE Certificate ADD CONSTRAINT certificate_primarykey PRIMARY KEY (id);

-- 
ALTER TABLE Certificate ADD CONSTRAINT foreignFile FOREIGN KEY (fileId) REFERENCES FileMeta(id) ON UPDATE CASCADE ON DELETE CASCADE;

-- 
ALTER TABLE Certificate ADD CONSTRAINT foreignProposal FOREIGN KEY (proposalId) REFERENCES Proposal(id) ON UPDATE CASCADE ON DELETE CASCADE;

--
-- TABLE: Service
-- 
--  

CREATE TABLE Service (
  id bigserial NOT NULL ,
  name character varying (1792) NOT NULL ,
  domain character varying (1792) NOT NULL ,
  description character varying (28672) NOT NULL ,
  needsPermissions boolean NOT NULL ,
  deprecated Boolean NOT NULL  DEFAULT FALSE,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE Service ADD CONSTRAINT service_primary_key PRIMARY KEY (id);

--
-- TABLE: ProposalService
-- 
--  

CREATE TABLE ProposalService (
  proposalId bigserial NOT NULL ,
  serviceId bigserial NOT NULL ,
  deliveryMethod character varying (20) NOT NULL ,
  source character varying (1792) NOT NULL 
);

-- 
ALTER TABLE ProposalService ADD CONSTRAINT proposal_service_primary_key PRIMARY KEY (proposalId,serviceId);

-- 
ALTER TABLE ProposalService ADD CONSTRAINT foreignProposal FOREIGN KEY (proposalId) REFERENCES Proposal(id) ON UPDATE CASCADE ON DELETE CASCADE;

-- 
ALTER TABLE ProposalService ADD CONSTRAINT foreignService FOREIGN KEY (serviceId) REFERENCES Service(id) ON UPDATE CASCADE ON DELETE CASCADE;

--
-- TABLE: Package
-- 
--  

CREATE TABLE Package (
  id bigserial NOT NULL ,
  name character varying (1792) NOT NULL ,
  deprecated Boolean NOT NULL  DEFAULT FALSE,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE Package ADD CONSTRAINT package_primary_key PRIMARY KEY (id);

--
-- TABLE: PackageService
-- 
--  

CREATE TABLE PackageService (
  packageId bigserial NOT NULL ,
  serviceId bigserial NOT NULL ,
  source character varying (1792) NOT NULL ,
  deliveryMethod character varying (20) NOT NULL 
);

-- 
ALTER TABLE PackageService ADD CONSTRAINT package_service_primary_key PRIMARY KEY (packageId,serviceId);

-- 
ALTER TABLE PackageService ADD CONSTRAINT foreignPackage FOREIGN KEY (packageId) REFERENCES Package(id) ON UPDATE CASCADE ON DELETE CASCADE;

-- 
ALTER TABLE PackageService ADD CONSTRAINT foreignService FOREIGN KEY (serviceId) REFERENCES Service(id) ON UPDATE CASCADE ON DELETE CASCADE;

--
-- TABLE: Contact
-- 
--  

CREATE TABLE Contact (
  id bigserial NOT NULL ,
  firstName character varying (1792) NOT NULL ,
  lastName character varying (1792) NOT NULL ,
  email character varying (1792) NOT NULL ,
  phoneNumber character varying (20) NOT NULL ,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW(),
  organisationId bigserial NOT NULL 
);

-- 
ALTER TABLE Contact ADD CONSTRAINT contact_primary_key PRIMARY KEY (id);

-- 
ALTER TABLE Contact ADD CONSTRAINT foreignOrganisation FOREIGN KEY (organisationId) REFERENCES Organisation(id) ON UPDATE CASCADE ON DELETE CASCADE;

--
-- TABLE: ContactProposal
-- 
--  

CREATE TABLE ContactProposal (
  contactId bigserial NOT NULL ,
  proposalId bigserial NOT NULL ,
  role character varying (40) NOT NULL 
);

-- 
ALTER TABLE ContactProposal ADD CONSTRAINT contactproposal_primarykey PRIMARY KEY (contactId,proposalId,role);

-- 
ALTER TABLE ContactProposal ADD CONSTRAINT foreignContact FOREIGN KEY (contactId) REFERENCES Contact(id) ON UPDATE CASCADE ON DELETE CASCADE;

-- 
ALTER TABLE ContactProposal ADD CONSTRAINT foreignProposal FOREIGN KEY (proposalId) REFERENCES Proposal(id) ON UPDATE CASCADE ON DELETE CASCADE;

--
-- TABLE: PackageProposal
-- 
--  

CREATE TABLE PackageProposal (
  packageId bigint NOT NULL ,
  proposalId bigint NOT NULL 
);

-- 
ALTER TABLE PackageProposal ADD CONSTRAINT packageproposal_primary_key PRIMARY KEY (packageId,proposalId);

-- 
ALTER TABLE PackageProposal ADD CONSTRAINT foreignPackage FOREIGN KEY (packageId) REFERENCES Package(id) ON UPDATE CASCADE ON DELETE CASCADE;

-- 
ALTER TABLE PackageProposal ADD CONSTRAINT foreignProposal FOREIGN KEY (proposalId) REFERENCES Proposal(id) ON UPDATE CASCADE ON DELETE CASCADE;

--
-- TABLE: UserInfoDeleted
-- Archief van alle verwijderde gebruikers.
--  

CREATE TABLE UserInfoDeleted (
  id bigint NOT NULL , -- Dezelfde primaire sleutel van voor deze User verwijderd werd.
  firstName character varying (1792) NOT NULL ,
  lastName character varying (1792) NOT NULL ,
  email character varying (1792) NOT NULL ,
  password character varying (1792) NOT NULL ,
  role character varying (20) NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE UserInfoDeleted ADD CONSTRAINT userinfodeleted_primary_key PRIMARY KEY (id);

--
-- TABLE: OrganisationDeleted
-- 
--  

CREATE TABLE OrganisationDeleted (
  id bigint NOT NULL ,
  organisationName character varying (1792) NOT NULL ,
  kboNumber character varying (10) NOT NULL ,
  ovoCode character varying (9),
  nisNumber character varying (5),
  serviceProvider character varying NOT NULL ,
  approved Boolean NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE OrganisationDeleted ADD CONSTRAINT organisationdeleted_primary_key PRIMARY KEY (id);

--
-- TABLE: MemberDeleted
-- 
--  

CREATE TABLE MemberDeleted (
  id bigint NOT NULL ,
  organisationId bigint NOT NULL ,
  userId bigint NOT NULL ,
  role character varying (20) NOT NULL ,
  accepted boolean NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE MemberDeleted ADD CONSTRAINT memberdeleted_primarykey PRIMARY KEY (id);

--
-- TABLE: PermissionDeleted
-- 
--  

CREATE TABLE PermissionDeleted (
  id bigint NOT NULL ,
  name character varying (1792) NOT NULL ,
  description character varying (28672) NOT NULL ,
  code character varying (1792) NOT NULL ,
  link character varying (1792) NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW(),
  proof character varying (1792) NOT NULL ,
  organisationId bigint NOT NULL 
);

-- 
ALTER TABLE PermissionDeleted ADD CONSTRAINT permissiondeleted_primary_key PRIMARY KEY (id);

--
-- TABLE: ProposalDeleted
-- 
--  

CREATE TABLE ProposalDeleted (
  id bigint NOT NULL ,
  name character varying (1792) NOT NULL ,
  status character varying (20) NOT NULL ,
  deadline timestamp with time zone,
  legalDeadline timestamp with time zone,
  tiDeadline timestamp with time zone,
  explanationDeadline character varying (28672),
  businessContext character varying (28672),
  legalContext character varying (28672),
  functionalSetup character varying (28672),
  technicalSetup character varying (28672),
  requiresPersonalData character varying (20),
  purposeRequestedData character varying (28672),
  extensionPreviousProposal character varying (20),
  originalTanNumber character varying (1792),
  originalUri character varying (1792),
  originalLegalContext character varying (28672),
  ftpAccount character varying (1792),
  requestsAreSpread character varying (20),
  estimatedNumberOfRequests character varying (20),
  feedback character varying (114688),
  inspection character varying (20) NOT NULL ,
  architectureVisualizationExplanation character varying (28672),
  architectureVisualizationId bigint,
  organisationId bigint NOT NULL ,
  authorizationPersonalDataId bigint,
  ftp character varying (20) NOT NULL ,
  externIpTest character varying (1792),
  externIpProd character varying (1792),
  externIpRangeTest character varying (1792),
  externIpRangeProd character varying (1792),
  sshKeyId bigint,
  reuseUri character varying (20) NOT NULL ,
  reuseLegalContext character varying (20) NOT NULL ,
  reuseFtpAccount character varying (20) NOT NULL ,
  reusePreviousProposal character varying (20) NOT NULL ,
  cnPersonalData character varying (1792),
  cnRepertorium character varying (1792),
  cnOther character varying (1792),
  magdaMessages character varying (20) NOT NULL ,
  onlineOption character varying (30),
  cooperationAgreementId bigint,
  processingAgreementId bigint,
  peaks bigint,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE ProposalDeleted ADD CONSTRAINT proposaldeleted_primary_key PRIMARY KEY (id);

--
-- TABLE: ContactDeleted
-- 
--  

CREATE TABLE ContactDeleted (
  id bigint NOT NULL ,
  firstName character varying (1792) NOT NULL ,
  lastName character varying (1792) NOT NULL ,
  email character varying (1792) NOT NULL ,
  phoneNumber character varying (20) NOT NULL ,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW(),
  organisationId bigint NOT NULL 
);

-- 
ALTER TABLE ContactDeleted ADD CONSTRAINT contactdeleted_primary_key PRIMARY KEY (id);

--
-- TABLE: ContactProposalDeleted
-- 
--  

CREATE TABLE ContactProposalDeleted (
  contactId bigint NOT NULL ,
  proposalId bigint NOT NULL ,
  role character varying (40) NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE ContactProposalDeleted ADD CONSTRAINT contactproposaldeleted_primarykey PRIMARY KEY (contactId,proposalId,role,deletedOn);

--
-- TABLE: ServiceDeleted
-- 
--  

CREATE TABLE ServiceDeleted (
  id bigint NOT NULL ,
  name character varying (1792) NOT NULL ,
  domain character varying (1792) NOT NULL ,
  description character varying (28672) NOT NULL ,
  needsPermissions boolean NOT NULL ,
  deprecated Boolean NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE ServiceDeleted ADD CONSTRAINT servicedeleted_primary_key PRIMARY KEY (id);

--
-- TABLE: ProposalServiceDeleted
-- 
--  

CREATE TABLE ProposalServiceDeleted (
  proposalId bigint NOT NULL ,
  serviceId bigint NOT NULL ,
  deliveryMethod character varying (20) NOT NULL ,
  source character varying (1792) NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE ProposalServiceDeleted ADD CONSTRAINT proposalservicedeleted_primary_key PRIMARY KEY (proposalId,serviceId,deletedOn);

--
-- TABLE: PackageDeleted
-- 
--  

CREATE TABLE PackageDeleted (
  id bigint NOT NULL ,
  name character varying (28672) NOT NULL ,
  deprecated Boolean NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE PackageDeleted ADD CONSTRAINT packagedeleted_primary_key PRIMARY KEY (id);

--
-- TABLE: PackageServiceDeleted
-- 
--  

CREATE TABLE PackageServiceDeleted (
  packageId bigint NOT NULL ,
  serviceId bigint NOT NULL ,
  source character varying (1792) NOT NULL ,
  deliveryMethod character varying (20) NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE PackageServiceDeleted ADD CONSTRAINT packageservicedeleted_primarykey PRIMARY KEY (packageId,serviceId,deletedOn);

--
-- TABLE: PackageProposalDeleted
-- 
--  

CREATE TABLE PackageProposalDeleted (
  packageId bigint NOT NULL ,
  proposalId bigint NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE PackageProposalDeleted ADD CONSTRAINT packageproposaldeleted_primary_key PRIMARY KEY (packageId,proposalId,deletedOn);

--
-- TABLE: ServiceDeliveryMethod
-- Stelt de beschikbare aanleveringsmethoden per dienst voor
--  

CREATE TABLE ServiceDeliveryMethod (
  serviceId bigint NOT NULL ,
  deliveryMethod character varying (20) NOT NULL 
);

-- 
ALTER TABLE ServiceDeliveryMethod ADD CONSTRAINT servicedeliverymethod_primarykey PRIMARY KEY (serviceId,deliveryMethod);

-- 
ALTER TABLE ServiceDeliveryMethod ADD CONSTRAINT foreignService FOREIGN KEY (serviceId) REFERENCES Service(id) ON UPDATE CASCADE ON DELETE CASCADE;

--
-- TABLE: ServiceSource
-- 
--  

CREATE TABLE ServiceSource (
  serviceId bigint NOT NULL ,
  source character varying (1792) NOT NULL 
);

-- 
ALTER TABLE ServiceSource ADD CONSTRAINT servicesource_primarykey PRIMARY KEY (serviceId,source);

-- 
ALTER TABLE ServiceSource ADD CONSTRAINT foreignService FOREIGN KEY (serviceId) REFERENCES Service(id) ON UPDATE CASCADE ON DELETE CASCADE;

--
-- TABLE: ServiceDeliveryMethodDeleted
-- 
--  

CREATE TABLE ServiceDeliveryMethodDeleted (
  serviceId bigint NOT NULL ,
  deliveryMethod character varying (20) NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE ServiceDeliveryMethodDeleted ADD CONSTRAINT servicedeliverymethoddeleted_primarykey PRIMARY KEY (serviceId,deliveryMethod,deletedOn);

--
-- TABLE: ServiceSourceDeleted
-- 
--  

CREATE TABLE ServiceSourceDeleted (
  serviceId bigint NOT NULL ,
  source character varying (1792) NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE ServiceSourceDeleted ADD CONSTRAINT servicesroucedeleted_primarykey PRIMARY KEY (serviceId,source,deletedOn);

--
-- TABLE: FileMetaDeleted
-- 
--  

CREATE TABLE FileMetaDeleted (
  id bigint NOT NULL ,
  fileLocation character varying (1792) NOT NULL ,
  fileSize bigint NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE FileMetaDeleted ADD CONSTRAINT filedeleted_primarykey PRIMARY KEY (id);

--
-- TABLE: CertificateDeleted
-- 
--  

CREATE TABLE CertificateDeleted (
  id bigint NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW(),
  fileId bigint NOT NULL ,
  proposalId bigint NOT NULL 
);

-- 
ALTER TABLE CertificateDeleted ADD CONSTRAINT certificatedeleted_primarykey PRIMARY KEY (id);

--
-- TABLE: ProposalOption
-- 
--  

CREATE TABLE ProposalOption (
  proposalId bigint NOT NULL ,
  option character varying (20) NOT NULL 
);

-- 
ALTER TABLE ProposalOption ADD CONSTRAINT proposaloption_primarykey PRIMARY KEY (proposalId,option);

-- 
ALTER TABLE ProposalOption ADD CONSTRAINT foreignProposal FOREIGN KEY (proposalId) REFERENCES Proposal(id) ON UPDATE CASCADE ON DELETE CASCADE;

--
-- TABLE: ProposalOptionDeleted
-- 
--  

CREATE TABLE ProposalOptionDeleted (
  proposalId bigint NOT NULL ,
  option character varying (20) NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE ProposalOptionDeleted ADD CONSTRAINT proposaloptiondeleted_primarykey PRIMARY KEY (proposalId,option,deletedOn);

ALTER TABLE UserInfo ADD CONSTRAINT proper_email CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$');

ALTER TABLE Contact ADD CONSTRAINT proper_email CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$');

-- Based on: https://x-team.com/blog/automatic-timestamps-with-postgresql/

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.lastupdated = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_timestamp_user
BEFORE UPDATE ON UserInfo
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TRIGGER set_timestamp_organisation
BEFORE UPDATE ON Organisation
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TRIGGER set_timestamp_organisation
    BEFORE UPDATE ON Contact
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TRIGGER set_timestamp_organisation
    BEFORE UPDATE ON Service
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TRIGGER set_timestamp_organisation
    BEFORE UPDATE ON Permission
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TRIGGER set_timestamp_organisation
    BEFORE UPDATE ON Package
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TRIGGER set_timestamp_organisation
    BEFORE UPDATE ON Certificate
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

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
---
--- Set Auto-timeout
---

ALTER DATABASE magdadatabase SET statement_timeout='30s';
ALTER ROLE admin SET statement_timeout='30s';
--- Dit voegt de extensie pg_trgm toe aan de Postgres databank.
--- Dit is nodig voor het benaderd zoeken.
CREATE EXTENSION pg_trgm;
---
--- Databank vullen met dummy-data
---

BEGIN;
--- Leegmaken vorige databank
DELETE FROM UserInfo;
DELETE FROM UserInfoDeleted;
DELETE FROM Organisation;
DELETE FROM OrganisationDeleted;
DELETE FROM Member;
DELETE FROM MemberDeleted;
DELETE FROM Contact;
DELETE FROM ContactDeleted;
DELETE FROM ContactProposal;
DELETE FROM ContactProposalDeleted;
DELETE FROM Proposal;
DELETE FROM ProposalDeleted;
DELETE FROM ProposalService;
DELETE FROM ProposalServiceDeleted;
DELETE FROM Service;
DELETE FROM ServiceDeleted;
DELETE FROM PackageService;
DELETE FROM PackageServiceDeleted;
DELETE FROM Package;
DELETE FROM PackageDeleted;
DELETE FROM Permission;
DELETE FROM PermissionDeleted;
DELETE FROM ServiceSource;
DELETE FROM ServiceSourceDeleted;
DELETE FROM ServiceDeliveryMethod;
DELETE FROM ServiceDeliveryMethodDeleted;
DELETE FROM PackageService;
DELETE FROM PackageServiceDeleted;
DELETE FROM Permission;
DELETE FROM PermissionDeleted;
DELETE FROM ContactProposal;
DELETE FROM ContactProposalDeleted;

--- Entities opnieuw aanmaken
COPY UserInfo (firstName, lastName, email, password, role) FROM '/selabrepo-back-dev/database/src/data/UserInfoData.csv' WITH (FORMAT csv);
COPY Organisation (organisationName, kboNumber, ovoCode, nisNumber, serviceProvider,
                   approved) FROM '/selabrepo-back-dev/database/src/data/OrganisationData.csv' WITH (FORMAT csv);
COPY Member (organisationId, userId, role, accepted) FROM '/selabrepo-back-dev/database/src/data/MemberData.csv' WITH (FORMAT csv);
COPY Proposal (name, status, deadline, legaldeadline, tideadline, explanationdeadline, businesscontext, legalcontext,
               functionalsetup, technicalsetup, requirespersonaldata, purposerequesteddata, extensionpreviousproposal,
               originaltannumber, originaluri, originallegalcontext, ftpaccount, requestsarespread,
               estimatednumberofrequests, feedback, inspection, architecturevisualizationexplanation,
               architecturevisualizationid, organisationid, authorizationpersonaldataid, ftp, externiptest,
               externipprod, externiprangetest, externiprangeprod, sshkeyid, reuseuri, reuselegalcontext,
               reuseftpaccount, reusepreviousproposal, cnpersonaldata, cnrepertorium, cnother, magdamessages,
               onlineoption, cooperationagreementid, processingagreementid,
               peaks) FROM '/selabrepo-back-dev/database/src/data/ProposalData.csv' WITH (FORMAT csv);
COPY Service (name, domain, description, needsPermissions) FROM '/selabrepo-back-dev/database/src/data/ServiceData.csv' WITH (FORMAT csv);
COPY ProposalService (proposalId, serviceId, deliveryMethod, source) FROM '/selabrepo-back-dev/database/src/data/ProposalServiceData.csv' WITH (FORMAT csv);
COPY Contact (firstName, lastName, email, phoneNumber, organisationid) FROM '/selabrepo-back-dev/database/src/data/ContactData.csv' WITH (FORMAT csv);
COPY ServiceDeliveryMethod (serviceid, deliverymethod) FROM '/selabrepo-back-dev/database/src/data/ServiceDeliveryMethodData.csv' WITH (FORMAT csv);
COPY ServiceSource (serviceid, source) FROM '/selabrepo-back-dev/database/src/data/ServiceSourceData.csv' WITH (FORMAT csv);
COPY Package (name, deprecated) FROM '/selabrepo-back-dev/database/src/data/PackageData.csv' WITH (FORMAT csv);
COPY PackageService (packageid, serviceid, source, deliverymethod) FROM '/selabrepo-back-dev/database/src/data/PackageServiceData.csv' WITH (FORMAT csv);
COPY PackageProposal (packageId, proposalId) FROM '/selabrepo-back-dev/database/src/data/PackageProposalData.csv' WITH (FORMAT csv);
COPY ContactProposal (contactid, proposalid, role) FROM '/selabrepo-back-dev/database/src/data/ContactProposalData.csv' WITH (FORMAT csv);

COMMIT;
