#!/bin/bash

echo "Resetting database to default values..."
sudo -H -u postgres psql -d magdadatabase -U admin -f fill_database.sql
