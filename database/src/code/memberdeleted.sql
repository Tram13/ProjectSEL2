
--
-- TABLE: MemberDeleted
-- 
--  

CREATE TABLE MemberDeleted (
  id bigint NOT NULL ,
  organisationId bigint NOT NULL ,
  userId bigint NOT NULL ,
  role character varying (20) NOT NULL ,
  accepted boolean NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE MemberDeleted ADD CONSTRAINT memberdeleted_primarykey PRIMARY KEY (id);
