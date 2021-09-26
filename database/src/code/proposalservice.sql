
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
