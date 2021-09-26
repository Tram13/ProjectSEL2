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
