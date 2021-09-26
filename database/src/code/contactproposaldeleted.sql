
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
