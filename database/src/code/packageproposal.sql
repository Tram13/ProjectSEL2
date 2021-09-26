
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
