
--
-- TABLE: FileMetaDeleted
-- 
--  

CREATE TABLE FileMetaDeleted (
  id bigint NOT NULL ,
  fileLocation character varying (1792) NOT NULL ,
  fileSize bigint NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE FileMetaDeleted ADD CONSTRAINT filedeleted_primarykey PRIMARY KEY (id);
