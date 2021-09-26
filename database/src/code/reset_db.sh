#!/bin/bash

echo "Resetting test database tables"
sudo -H -u postgres psql -d magdadatabase-test -U admin -f all_schemas.sql
echo "Resetting database tables and filling with default values..."
sudo -H -u postgres psql -d magdadatabase -U admin -f all_schemas_with_default_values.sql
