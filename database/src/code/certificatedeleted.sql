
--
-- TABLE: CertificateDeleted
-- 
--  

CREATE TABLE CertificateDeleted (
  id bigint NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW(),
  fileId bigint NOT NULL ,
  proposalId bigint NOT NULL 
);

-- 
ALTER TABLE CertificateDeleted ADD CONSTRAINT certificatedeleted_primarykey PRIMARY KEY (id);
