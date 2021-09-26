-- Based on: https://x-team.com/blog/automatic-timestamps-with-postgresql/

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.lastupdated = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_timestamp_user
BEFORE UPDATE ON UserInfo
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TRIGGER set_timestamp_organisation
BEFORE UPDATE ON Organisation
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TRIGGER set_timestamp_organisation
    BEFORE UPDATE ON Contact
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TRIGGER set_timestamp_organisation
    BEFORE UPDATE ON Service
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TRIGGER set_timestamp_organisation
    BEFORE UPDATE ON Permission
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TRIGGER set_timestamp_organisation
    BEFORE UPDATE ON Package
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TRIGGER set_timestamp_organisation
    BEFORE UPDATE ON Certificate
    FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();

