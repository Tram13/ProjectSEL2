
--
-- TABLE: Package
-- 
--  

CREATE TABLE Package (
  id bigserial NOT NULL ,
  name character varying (1792) NOT NULL ,
  deprecated Boolean NOT NULL  DEFAULT FALSE,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE Package ADD CONSTRAINT package_primary_key PRIMARY KEY (id);
