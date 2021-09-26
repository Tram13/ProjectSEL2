
--
-- TABLE: PackageDeleted
-- 
--  

CREATE TABLE PackageDeleted (
  id bigint NOT NULL ,
  name character varying (28672) NOT NULL ,
  deprecated Boolean NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE PackageDeleted ADD CONSTRAINT packagedeleted_primary_key PRIMARY KEY (id);
