
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
