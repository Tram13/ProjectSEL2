
--
-- TABLE: Contact
-- 
--  

CREATE TABLE Contact (
  id bigserial NOT NULL ,
  firstName character varying (1792) NOT NULL ,
  lastName character varying (1792) NOT NULL ,
  email character varying (1792) NOT NULL ,
  phoneNumber character varying (20) NOT NULL ,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW(),
  organisationId bigserial NOT NULL 
);

-- 
ALTER TABLE Contact ADD CONSTRAINT contact_primary_key PRIMARY KEY (id);

-- 
ALTER TABLE Contact ADD CONSTRAINT foreignOrganisation FOREIGN KEY (organisationId) REFERENCES Organisation(id) ON UPDATE CASCADE ON DELETE CASCADE;
