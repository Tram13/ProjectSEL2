
--
-- TABLE: PackageServiceDeleted
-- 
--  

CREATE TABLE PackageServiceDeleted (
  packageId bigint NOT NULL ,
  serviceId bigint NOT NULL ,
  source character varying (1792) NOT NULL ,
  deliveryMethod character varying (20) NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE PackageServiceDeleted ADD CONSTRAINT packageservicedeleted_primarykey PRIMARY KEY (packageId,serviceId,deletedOn);
