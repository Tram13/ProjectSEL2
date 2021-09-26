
--
-- TABLE: FileMeta
-- 
--  

CREATE TABLE FileMeta (
  id bigserial NOT NULL ,
  fileLocation character varying (1792) NOT NULL ,
  fileSize bigint NOT NULL ,
  created timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE FileMeta ADD CONSTRAINT file_primarykey PRIMARY KEY (id);
