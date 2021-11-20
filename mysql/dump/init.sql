CREATE DATABASE auth_mysql;

SET @q1 = concat('CREATE USER "user"@"localhost" IDENTIFIED BY "', @MYSQL_PASSWORD, '" ');
PREPARE stmt FROM @q1; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @q2 = concat('CREATE USER "user"@"%" IDENTIFIED BY "', @MYSQL_PASSWORD, '" ');
PREPARE stmt2 FROM @q2; EXECUTE stmt2; DEALLOCATE PREPARE stmt2;

GRANT SELECT ON auth_mysql.* to 'user'@'localhost';
GRANT INSERT ON auth_mysql.* to 'user'@'localhost';
GRANT DELETE ON auth_mysql.* to 'user'@'localhost';
GRANT UPDATE ON auth_mysql.* to 'user'@'localhost';
GRANT SELECT ON auth_mysql.* to 'user'@'%';
GRANT INSERT ON auth_mysql.* to 'user'@'%';
GRANT DELETE ON auth_mysql.* to 'user'@'%';
GRANT UPDATE ON auth_mysql.* to 'user'@'%';

USE auth_mysql

create table if not exists oauth_client_details (
  client_id VARCHAR(255) PRIMARY KEY,
  resource_ids VARCHAR(255),
  client_secret VARCHAR(255),
  scope VARCHAR(255),
  authorized_grant_types VARCHAR(255),
  web_server_redirect_uri VARCHAR(255),
  authorities VARCHAR(255),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(4096),
  autoapprove VARCHAR(255)
);

create table if not exists oauth_client_token (
  token_id VARCHAR(255),
  token BLOB,
  authentication_id VARCHAR(255) PRIMARY KEY,
  user_name VARCHAR(255),
  client_id VARCHAR(255)
);

create table if not exists oauth_access_token (
  token_id VARCHAR(255),
  token BLOB,
  authentication_id VARCHAR(255) PRIMARY KEY,
  user_name VARCHAR(255),
  client_id VARCHAR(255),
  authentication BLOB,
  refresh_token VARCHAR(255)
);

create table if not exists oauth_refresh_token (
  token_id VARCHAR(255),
  token BLOB,
  authentication BLOB
);

create table if not exists oauth_code (
  code VARCHAR(255), authentication BLOB
);

create table if not exists oauth_approvals (
    userId VARCHAR(255),
    clientId VARCHAR(255),
    scope VARCHAR(255),
    status VARCHAR(10),
    expiresAt TIMESTAMP DEFAULT  CURRENT_TIMESTAMP,
    lastModifiedAt TIMESTAMP DEFAULT  CURRENT_TIMESTAMP
);

create table if not exists ClientDetails (
  appId VARCHAR(255) PRIMARY KEY,
  resourceIds VARCHAR(255),
  appSecret VARCHAR(255),
  scope VARCHAR(255),
  grantTypes VARCHAR(255),
  redirectUrl VARCHAR(255),
  authorities VARCHAR(255),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additionalInformation VARCHAR(4096),
  autoApproveScopes VARCHAR(255)
);
