
--
-- TABLE: ServiceSource
-- 
--  

CREATE TABLE ServiceSource (
  serviceId bigint NOT NULL ,
  source character varying (1792) NOT NULL 
);

-- 
ALTER TABLE ServiceSource ADD CONSTRAINT servicesource_primarykey PRIMARY KEY (serviceId,source);

-- 
ALTER TABLE ServiceSource ADD CONSTRAINT foreignService FOREIGN KEY (serviceId) REFERENCES Service(id) ON UPDATE CASCADE ON DELETE CASCADE;
