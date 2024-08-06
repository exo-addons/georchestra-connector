# eXo Georchestra connector

This addon is using to synchronize Georchestra roles with eXo spaces memberships.

With this addon, you will be able to bind a geOrchestra in a space. Then, when a user joins the space, eXo send a request to geOrchestra to add the user in the role.

In parallel, this addon adds 2 rest endpoints to notify eXo when a user is added in a geOrchestra role. When these endpoints are called, the specified user is added/remove from spaces bind with the specified roles

# Configuration

3 properties are used in this addon

| Properties                     | Default    | Description            |
----------------------|----------------|-------------------------------|
| `org.georchestra.url`|      | Set the url of geOrchestra component.       |
| `org.georchestra.username`|      | Set the username of the service account which call geOrchestra.       |
| `org.georchestra.password`|      | Set the password of the service account which call geOrchestra.       |


# REST Endpoints

## Add user : 

POST /rest/private/georchestra
Header : Authorization: Basic cmDvd3pgYXNzd29yZA
Body :

`{
"role":"geOrchestraRole"
"username": "john"
}`

The authorization header is a classic Basic Authentication Header, build like this : 
'Basic Base64encode(username:password)'

- role : the name of the geOrchestraRole
- user : the user to add in space

When calling this endpoint, user `john` will be added in all spaces bind to the role `geOrchestraRole`

## Remove user :

DELETE /rest/private/georchestra
Header : Authorization: Basic cmDvd3pgYXNzd29yZA
Body :

`{
"role":"geOrchestraRole"
"username": "john"
}`

The authorization header is a classic Basic Authentication Header, build like this :
'Basic Base64encode(username:password)'

- role : the name of the geOrchestraRole
- user : the user to add in space

When calling this endpoint, user `john` will be removed from all spaces bind to the role `geOrchestraRole`
One exception : if the user is the last manager of the space, he will not be removed.


