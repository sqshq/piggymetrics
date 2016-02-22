/**
 * Creates microservices internal auth credentials collection
 */

print('dump start');

db.clients.update(
    { "_id": "browser" },
    {
        "_id": "browser",
        "authorizedGrantTypes": [
            "refresh_token",
            "password"
        ],
        "scopes": [
            "ui"
        ],
        "secretRequired": false
    },
    { upsert: true }
);

db.clients.update(
    { "_id": "account-service" },
    {
        "_id": "account-service",
        "secret": "$2a$04$oD.lYtQTakG12fU1FG4ssOA2Zv16ULoZHU0uc4b8UpKTfnY8NPk.e",
        "authorizedGrantTypes": [
            "refresh_token",
            "client_credentials"
        ],
        "scopes": [
            "server"
        ],
        "secretRequired": true
    },
    { upsert: true }
);

db.clients.update(
    { "_id": "statistics-service" },
    {
        "_id": "statistics-service",
        "secret": "$2a$04$oD.lYtQTakG12fU1FG4ssOA2Zv16ULoZHU0uc4b8UpKTfnY8NPk.e",
        "authorizedGrantTypes": [
            "refresh_token",
            "client_credentials"
        ],
        "scopes": [
            "server"
        ],
        "secretRequired": true
    },
    { upsert: true }
);

db.clients.update(
    { "_id": "notification-service" },
    {
        "_id": "notification-service",
        "secret": "$2a$04$oD.lYtQTakG12fU1FG4ssOA2Zv16ULoZHU0uc4b8UpKTfnY8NPk.e",
        "authorizedGrantTypes": [
            "refresh_token",
            "client_credentials"
        ],
        "scopes": [
            "server"
        ],
        "secretRequired": true
    },
    { upsert: true }
);

print('dump complete');