
--
-- TABLE: UserInfoDeleted
-- Archief van alle verwijderde gebruikers.
--  

CREATE TABLE UserInfoDeleted (
  id bigint NOT NULL , -- Dezelfde primaire sleutel van voor deze User verwijderd werd.
  firstName character varying (1792) NOT NULL ,
  lastName character varying (1792) NOT NULL ,
  email character varying (1792) NOT NULL ,
  password character varying (1792) NOT NULL ,
  role character varying (20) NOT NULL ,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE UserInfoDeleted ADD CONSTRAINT userinfodeleted_primary_key PRIMARY KEY (id);
