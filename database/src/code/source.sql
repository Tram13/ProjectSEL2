
--
-- TABLE: Source
-- 
--  

CREATE TABLE Source (
  id bigserial NOT NULL ,
  name character varying (1792) NOT NULL 
);

-- 
ALTER TABLE Source ADD CONSTRAINT source_primarykey PRIMARY KEY (id);
