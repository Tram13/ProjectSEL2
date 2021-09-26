
--
-- TABLE: Permission
-- "Machtiging"
-- 
-- 28672 tekens voor de beschrijving komt overeen met maximale lengte van een
-- encoded HTML-entity (7) vermenigvuldigd met maximaal aantal invoertekens (4096).
--  

CREATE TABLE Permission (
  id bigserial NOT NULL ,
  name character varying (1792) NOT NULL ,
  description character varying (28672) NOT NULL ,
  code character varying (1792) NOT NULL ,
  link character varying (1792) NOT NULL ,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW(),
  proof bigint NOT NULL ,
  organisationId bigint NOT NULL 
);

-- 
ALTER TABLE Permission ADD CONSTRAINT permission_primary_key PRIMARY KEY (id);

-- 
ALTER TABLE Permission ADD CONSTRAINT foreignOrganisation FOREIGN KEY (organisationId) REFERENCES Organisation(id) ON UPDATE CASCADE ON DELETE CASCADE;

-- 
ALTER TABLE Permission ADD CONSTRAINT foreignFile FOREIGN KEY (proof) REFERENCES FileMeta(id) ON UPDATE CASCADE ON DELETE CASCADE;
