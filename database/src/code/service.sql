
--
-- TABLE: Service
-- 
--  

CREATE TABLE Service (
  id bigserial NOT NULL ,
  name character varying (1792) NOT NULL ,
  domain character varying (1792) NOT NULL ,
  description character varying (28672) NOT NULL ,
  needsPermissions boolean NOT NULL ,
  deprecated Boolean NOT NULL  DEFAULT FALSE,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE Service ADD CONSTRAINT service_primary_key PRIMARY KEY (id);
