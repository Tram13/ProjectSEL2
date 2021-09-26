
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
