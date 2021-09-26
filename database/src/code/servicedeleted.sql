
--
-- TABLE: ServiceDeleted
-- 
--  

CREATE TABLE ServiceDeleted (
  id bigint NOT NULL ,
  name character varying (1792) NOT NULL ,
  domain character varying (1792) NOT NULL ,
  description character varying (28672) NOT NULL ,
  needsPermissions boolean NOT NULL ,
  deprecated Boolean NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE ServiceDeleted ADD CONSTRAINT servicedeleted_primary_key PRIMARY KEY (id);
