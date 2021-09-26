#!/bin/bash

# Dit script genereerd een .sql-bestand om de Postgres-databank aan te maken

# Dit houdt de status bij
counter=0

# Vorige databank verwijderen
echo "-- Deze code reset de databank, alle gegevens worden verwijderd!
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO admin;
GRANT ALL ON SCHEMA public TO public;
" > all_schemas.sql
status=$?
((counter+=status))

# Diagram-code toevoegen aan eindcode
cat organisation.sql userinfo.sql member.sql filemeta.sql permission.sql proposal.sql certificate.sql service.sql \
    proposalservice.sql package.sql packageservice.sql contact.sql contactproposal.sql packageproposal.sql \
    userinfodeleted.sql organisationdeleted.sql memberdeleted.sql permissiondeleted.sql proposaldeleted.sql \
    contactdeleted.sql contactproposaldeleted.sql servicedeleted.sql proposalservicedeleted.sql packagedeleted.sql \
    packageservicedeleted.sql packageproposaldeleted.sql servicedeliverymethod.sql servicesource.sql \
    servicedeliverymethoddeleted.sql servicesourcedeleted.sql filemetadeleted.sql certificatedeleted.sql \
    proposaloption.sql proposaloptiondeleted.sql >> all_schemas.sql

status=$?
((counter+=status))

# Extra code voor email-constraint UserInfo
echo -n "
ALTER TABLE UserInfo ADD CONSTRAINT proper_email CHECK (email ~* " >> all_schemas.sql
cat email_regex.re >> all_schemas.sql
status=$?
((counter+=status))
echo ");" >> all_schemas.sql

# Extra code voor email-constraint Contact
echo -n "
ALTER TABLE Contact ADD CONSTRAINT proper_email CHECK (email ~* " >> all_schemas.sql
cat email_regex.re >> all_schemas.sql
status=$?
((counter+=status))
echo -e ");\n" >> all_schemas.sql

# Extra code voor auto timestamping
cat timestamp_trigger.sql >> all_schemas.sql
status=$?
((counter+=status))

# Automatisch archiveren van verwijderde items
cat archive_on_delete.sql >> all_schemas.sql
status=$?
((counter+=status))

# Auto-timeout instellen
cat timeout.sql >> all_schemas.sql
status=$?
((counter+=status))

# Toevoegen extensions
cat extensions.sql >> all_schemas.sql
status=$?
((counter+=status))

# Databank opnieuw vullen met dummy-data
cat all_schemas.sql fill_database.sql > all_schemas_with_default_values.sql
status=$?
((counter+=status))

if [ "$counter" -eq 0 ]; then
    echo -e "SQL-code succesvol samengeplakt\n\033[0;31mBij uitvoering wordt de databank gereset!"
else
    echo "Fout bij samenplakken SQL-code"
fi
