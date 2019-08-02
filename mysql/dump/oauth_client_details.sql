SET @token_validity = 3600;

INSERT INTO oauth_client_details
    (client_id, client_secret, scope, authorized_grant_types, access_token_validity, additional_information)

VALUES
    ('browser', '', 'ui', 'refresh_token,password', @token_validity, '{}'),
    ('account-service', @ACCOUNT_SERVICE_PASSWORD, 'server', 'client_credentials,refresh_token', @token_validity, '{}'),
    ('statistics-service', @STATISTICS_SERVICE_PASSWORD, 'server', 'client_credentials,refresh_token', @token_validity, '{}'),
    ('notification-service', @NOTIFICATION_SERVICE_PASSWORD, 'server', 'client_credentials,refresh_token', @token_validity, '{}')
    ;