
--
-- TABLE: OrganisationDeleted
-- 
--  

CREATE TABLE OrganisationDeleted (
  id bigint NOT NULL ,
  organisationName character varying (1792) NOT NULL ,
  kboNumber character varying (10) NOT NULL ,
  ovoCode character varying (9),
  nisNumber character varying (5),
  serviceProvider character varying NOT NULL ,
  approved Boolean NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE OrganisationDeleted ADD CONSTRAINT organisationdeleted_primary_key PRIMARY KEY (id);
