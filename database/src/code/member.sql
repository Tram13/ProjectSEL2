
--
-- TABLE: Member
-- 
--  

CREATE TABLE Member (
  id bigserial NOT NULL ,
  organisationId bigserial NOT NULL ,
  userId bigserial NOT NULL ,
  role character varying (20) NOT NULL ,
  accepted boolean NOT NULL  DEFAULT FALSE,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE Member ADD CONSTRAINT member_primary_key PRIMARY KEY (id);

-- 
ALTER TABLE Member ADD CONSTRAINT foreignUser FOREIGN KEY (userId) REFERENCES UserInfo(id) ON UPDATE CASCADE ON DELETE CASCADE;

-- 
ALTER TABLE Member ADD CONSTRAINT foreignOrganisation FOREIGN KEY (organisationId) REFERENCES Organisation(id) ON UPDATE CASCADE ON DELETE CASCADE;
