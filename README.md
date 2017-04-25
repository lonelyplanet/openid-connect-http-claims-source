## Simple HTTP Based Claims Source

Slightly modified version of [Connect2id Simple HTTP Based Claims Source](https://bitbucket.org/connect2id/openid-connect-http-claims-source)

### Notable changes

- Renamed packages to match other Lonely Planet projects
- Fixed broken unit test

---

Connector for sourcing OpenID Connect claims about a subject (end-user) from an HTTP endpoint. 

Implements the com.nimbusds.openid.connect.provider.spi.claims.ClaimsSource
SPI.

Overview of the HTTP connector:

 * Supports retrieval of arbitrary OpenID Connect claims.

 * Supports multiple scripts and languages via language tags.

 * Access to the HTTP endpoint requires a non-expiring bearer token.

 * Utilises an HTTP POST request to obtain the claims in order to prevent
   leaking of the request parameters (subject identifier and claim names) into
   HTTP server logs.

 * Configured by a Java properties file /WEB-INF/httpClaimsSource.properties .
   Individual properties may be overridden using system properties (e.g. passed
   via the command line at JVM startup).



Example configuration properties:

op.httpClaimsSource.enable = true
op.httpClaimsSource.url = https://example.com/claims-source
op.httpClaimsSource.connectTimeout = 250
op.httpClaimsSource.readTimeout = 250
op.httpClaimsSource.trustSelfSignedCerts = false
op.httpClaimsSource.apiAccessToken = ztucZS1ZyFKgh0tUEruUtiSTXhnexmd6



Example claims request to the HTTP endpoint:

POST /claims-source HTTP/1.1
Host: www.example.com
Content-Type: application/json; charset=UTF-8
Authorization: Bearer ztucZS1ZyFKgh0tUEruUtiSTXhnexmd6

{
  "sub"    : "alice",
  "claims" : [ "email", "email_verified", "name", "given_name", "family_name" ]
}



Example response:

HTTP/1.1 200 OK
Date: Mon, 23 May 2016 22:38:34 GMT
Content-Type: application/json; charset=UTF-8

{
  "sub"            : "alice",
  "email"          : "alice@wonderland.net",
  "email_verified" : true,
  "name"           : "Alice Adams",
  "given_name"     : "Alice",
  "family_name"    : "Adams"
}


Copyright (c) Connect2idLtd., 2016
