# Spring Boot Jwt Authentication Server
Spring Boot Application which using Jason Web Token (JWT) for authentication process and in memory H2 datastore.

## Getting started
Import this project in you IDE and let Maven download dependencies in pom.xml file.

## Default users
Default users will be generated on start:

| ID  | Username  | Email               | Password        | Roles |
| :-: | :-------: | :-----------------: | :-------------: | :---: |
| 1   | admin     | admin@gmail.com     | secret_password | ADMIN |
| 2   | simpleUser| simpleUser@gmail.com| secret_password | USER  |
| 3   | alex      | alex@gmail.com      | secret_password | USER  |
| 4   | greg      | greg@gmail.com      | secret_password | USER  |
| 5   | helen     | helen@gmail.com     | secret_password | USER  |


Also discover list of default users by accessing endpoint:
```
{server_port}/info
```
Every time as you run app - it will generate JWT for users: *admin* and *simpleUser*.
You can discover tokens in logs.

## Endpoints\
To register a new user you need to send POST request with HEADER - 'application/json' and BODY:
```
{
  "username":"{username}",
  "email":"{email}",
  "password":"{user password}"
}
```
On a following endpoint:
```
{server_port}/auth/register
```

App will response with BODY:
```
{
  "sucess":"{true or false}",
  "message":"{Appropriate message}"
}
```

For login send POST request with BODY:
```
{
  "usernameOrEmail":"{username/email}",
  "password":"{user password}"
}
```
On a following endpoint:
```
{server_port}/auth/login
```
and get response with HEADER - 'Authentication: Bearer {provided_token}'.


Endpoint accessible only by user with role 'ADMIN' :
```
{server_port}/web-api/users
```
responsing with JSON list of users in following format:
```
    {
        "username": "admin",
        "email": "admin@gmail.com",
        "enabled": true,
        "roles": [
            "ADMIN"
        ],
        "date": {
            "createdAt": "{created time}",
            "updatedAt": "{updated time}"
        },
        "authorities": [
            "ADMIN"
        ],
        "accountNonExpired": true,
        "accountNonLocked": true,
        "credentialsNonExpired": true
    }
```
