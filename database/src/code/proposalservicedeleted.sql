
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
