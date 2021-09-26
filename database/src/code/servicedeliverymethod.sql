
--
-- TABLE: ServiceDeliveryMethod
-- Stelt de beschikbare aanleveringsmethoden per dienst voor
--  

CREATE TABLE ServiceDeliveryMethod (
  serviceId bigint NOT NULL ,
  deliveryMethod character varying (20) NOT NULL 
);

-- 
ALTER TABLE ServiceDeliveryMethod ADD CONSTRAINT servicedeliverymethod_primarykey PRIMARY KEY (serviceId,deliveryMethod);

-- 
ALTER TABLE ServiceDeliveryMethod ADD CONSTRAINT foreignService FOREIGN KEY (serviceId) REFERENCES Service(id) ON UPDATE CASCADE ON DELETE CASCADE;
