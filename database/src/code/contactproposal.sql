
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
