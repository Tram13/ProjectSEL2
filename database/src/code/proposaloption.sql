
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
