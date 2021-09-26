#!/bin/bash

# Directory updaten
git checkout backup
git pull

# Bestandsnaam instellen
w=$(date +"%Y_%m_%d")
file_location="/backup/backup/db/magdadatabase_backup_$w.sql.tar"
file_location_encoded="$file_location.enc"

# De user 'postgres' moet lees- en schrijfrechten hebben op $file_location
sudo -u postgres pg_dump -d magdadatabase -f "$file_location" -F tar -v
openssl smime -encrypt -binary -text -aes256 -out "$file_location_encoded" -outform DER /root/cert.pem < "$file_location"
rm "$file_location"

# Kopie online plaatsen
git add "$file_location_encoded"
git commit -m "Updated database backup"
git push
