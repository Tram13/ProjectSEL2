
--
-- TABLE: PermissionDeleted
-- 
--  

CREATE TABLE PermissionDeleted (
  id bigint NOT NULL ,
  name character varying (1792) NOT NULL ,
  description character varying (28672) NOT NULL ,
  code character varying (1792) NOT NULL ,
  link character varying (1792) NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW(),
  proof character varying (1792) NOT NULL ,
  organisationId bigint NOT NULL 
);

-- 
ALTER TABLE PermissionDeleted ADD CONSTRAINT permissiondeleted_primary_key PRIMARY KEY (id);
