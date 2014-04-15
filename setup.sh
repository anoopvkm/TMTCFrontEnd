#!/bin/bash
# Author : Anoop R Santhosh
# This script sets up databases and other background setup details
# This has to be run only once on a system .

echo  "Enter MySQL root password : "
read -s rootpw

echo  "Creating a Database for TMTC front end "
mysql -u root -p$rootpw << EOF
CREATE DATABASE TMTCFrontEnd
EOF

if [ $? != "0" ]; then
	echo "[Error] : Database creation failed "
	exit 1
else
	echo "Database created successfully "
fi



















