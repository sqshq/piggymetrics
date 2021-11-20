#!/bin/bash

echo "setup env variables in MySQL"
init_vars=(
    'SET @ACCOUNT_SERVICE_PASSWORD = '"'$ACCOUNT_SERVICE_PASSWORD'"'; '
    'SET @STATISTICS_SERVICE_PASSWORD = '"'$STATISTICS_SERVICE_PASSWORD'"'; '
    'SET @NOTIFICATION_SERVICE_PASSWORD = '"'$NOTIFICATION_SERVICE_PASSWORD'"';'
    'SET @MYSQL_PASSWORD = '"'$MYSQL_PASSWORD'"';'
)
# Join the array init_vars in a string and add oauth_client_details.sql content to it
query="$(IFS=; echo "${init_vars[*]}") $(< init.sql) $(< oauth_client_details.sql)"

# Store content of query inside a temporary file
tmp_file=$(mktemp /tmp/duplicate.XXXXXXX --suffix vars.sql)
echo "${query}" > "${tmp_file}"

echo "Run sql scripts"
mysql -uroot -p${MYSQL_PASSWORD} < "${tmp_file}"

# Remove temporary file
rm "${tmp_file}"