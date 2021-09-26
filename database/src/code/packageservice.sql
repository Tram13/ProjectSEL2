
--
-- TABLE: PackageService
-- 
--  

CREATE TABLE PackageService (
  packageId bigserial NOT NULL ,
  serviceId bigserial NOT NULL ,
  source character varying (1792) NOT NULL ,
  deliveryMethod character varying (20) NOT NULL 
);

-- 
ALTER TABLE PackageService ADD CONSTRAINT package_service_primary_key PRIMARY KEY (packageId,serviceId);

-- 
ALTER TABLE PackageService ADD CONSTRAINT foreignPackage FOREIGN KEY (packageId) REFERENCES Package(id) ON UPDATE CASCADE ON DELETE CASCADE;

-- 
ALTER TABLE PackageService ADD CONSTRAINT foreignService FOREIGN KEY (serviceId) REFERENCES Service(id) ON UPDATE CASCADE ON DELETE CASCADE;
