
--
-- TABLE: UserInfo
-- PostGreSQL laat de tabelnaam "User" niet toe. Daarom deze aangepaste naamgeving.
-- 
-- De lengte (1792) is gekozen door de langste encoded HTML entity (7) te nemen en
-- die dan te vermenigvuldigen met het maximaal aantal tekens (256).
--  

CREATE TABLE UserInfo (
  id bigserial NOT NULL ,
  firstName character varying (1792) NOT NULL ,
  lastName character varying (1792) NOT NULL ,
  email character varying (1792) NOT NULL , -- Bij email wordt er een simpele regex toegevoegd die controleert of het emailadres geldig is.
  password character varying (1792) NOT NULL ,
  role character varying (20) NOT NULL ,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE UserInfo ADD CONSTRAINT user_primary_key PRIMARY KEY (id);

-- 
ALTER TABLE UserInfo ADD CONSTRAINT email_unique_constraint UNIQUE (email);
