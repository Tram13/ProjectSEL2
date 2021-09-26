
--
-- TABLE: ServiceDeliveryMethodDeleted
-- 
--  

CREATE TABLE ServiceDeliveryMethodDeleted (
  serviceId bigint NOT NULL ,
  deliveryMethod character varying (20) NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE ServiceDeliveryMethodDeleted ADD CONSTRAINT servicedeliverymethoddeleted_primarykey PRIMARY KEY (serviceId,deliveryMethod,deletedOn);
