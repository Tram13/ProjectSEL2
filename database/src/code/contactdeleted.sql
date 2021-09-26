
--
-- TABLE: ContactDeleted
-- 
--  

CREATE TABLE ContactDeleted (
  id bigint NOT NULL ,
  firstName character varying (1792) NOT NULL ,
  lastName character varying (1792) NOT NULL ,
  email character varying (1792) NOT NULL ,
  phoneNumber character varying (20) NOT NULL ,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW(),
  organisationId bigint NOT NULL 
);

-- 
ALTER TABLE ContactDeleted ADD CONSTRAINT contactdeleted_primary_key PRIMARY KEY (id);
