
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
