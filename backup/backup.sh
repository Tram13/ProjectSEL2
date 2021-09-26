#!/bin/bash

# script om snel een backup te maken van server bestanden


cd /backup/backup/ || exit 1
git checkout backup || exit 2

cp /etc/nginx/sites-available/sel2-2.ugent.be ./nginx/sel2-2.ugent.be
cp /selab/main.cpp ./data/main.cpp
cp /selab/templates/index.html ./data/templates/index.html
cp /selab/static/styles.css ./data/static/styles.css
cp /etc/systemd/system/selab.service ./servicefiles/selab.service
cp /etc/systemd/system/api.service ./servicefiles/api.service
cp /etc/systemd/system/api-dev.service ./servicefiles/api-dev.service
cp /etc/systemd/system/pgadmin.service ./servicefiles/pgadmin.service
crontab -l > ./crontab.txt

git add .
git commit -am "backup"
git push
