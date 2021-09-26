
--
-- TABLE: Organisation
-- 
--  

CREATE TABLE Organisation (
  id bigserial NOT NULL ,
  organisationName character varying (1792) NOT NULL ,
  kboNumber character varying (10) NOT NULL ,
  ovoCode character varying (9),
  nisNumber character varying (5),
  serviceProvider character varying (1792) NOT NULL ,
  approved Boolean NOT NULL  DEFAULT FALSE,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE Organisation ADD CONSTRAINT organisation_primary_key PRIMARY KEY (id);

-- 
ALTER TABLE Organisation ADD CONSTRAINT uniqueOrganisationConstraint UNIQUE (kboNumber,ovoCode,nisNumber);
