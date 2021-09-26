
--
-- TABLE: ServiceSourceDeleted
-- 
--  

CREATE TABLE ServiceSourceDeleted (
  serviceId bigint NOT NULL ,
  source character varying (1792) NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE ServiceSourceDeleted ADD CONSTRAINT servicesroucedeleted_primarykey PRIMARY KEY (serviceId,source,deletedOn);
