[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-dist-root]: https://github.com/inverno-io/inverno-dist
[inverno-tool-maven-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-maven-plugin
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[inverno-mod-http-server]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-http-server/
[inverno-mod-web-server]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-web-server/
[inverno-mod-security]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-security/
[inverno-mod-security-http]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-security-http/
[inverno-mod-security-jose]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-security-jose/
[inverno-mod-security-ldap]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-security-ldap/

[ldap]: https://en.wikipedia.org/wiki/Lightweight_Directory_Access_Protocol
[csrf]: https://en.wikipedia.org/wiki/Cross-site_request_forgery
[cors]: https://en.wikipedia.org/wiki/Cross-origin_resource_sharing
[jws]: https://datatracker.ietf.org/doc/html/rfc7515
[jwe]: https://datatracker.ietf.org/doc/html/rfc7516
[jwk]: https://datatracker.ietf.org/doc/html/rfc7517
[jwa]: https://datatracker.ietf.org/doc/html/rfc7518
[jwt]: https://datatracker.ietf.org/doc/html/rfc7519
[http_basic]: https://datatracker.ietf.org/doc/html/rfc7617
[http_digest]: https://datatracker.ietf.org/doc/html/rfc7616
[docker_compose]: https://docs.docker.com/compose/
[openldap]: https://www.openldap.org/

# Inverno Web security example

A sample Inverno application showing how to use the Security modules to secure a Web application.

The configuration is exposed in the module's configuration `AppConfiguration` and allows to configure the web server and the [LDAP][ldap] client module.

The application demonstrates various login and authentication approaches as well as access control and user identification. All the these services are provided by the Inverno security modules:

- [Inverno security][inverno-mod-security] which provides core security components
- [Inverno security HTTP][inverno-mod-security-http] which provides HTTP specific security components including HTTP authentication schemes, [CSRF][csrf] protection, CORS[cors], form-based login, token-based authentication...
- [Inverno security JOSE][inverno-mod-security-jose] which is a complete implementation of JSON Web Signature (JWS), JSON Web Encryption (JWE), JSON Web Key (JWK), JSON Web Algorithms (JWA), JSON Web Token (JWT)... This module is particularly useful to generate authentication token.
- [Inverno security LDAP][inverno-mod-security-ldap] which provides LDAP and Active Directory authenticator implementation as well as identity resolver.

It defines the following Web configurers:

- `BasicRouterConfigurer` which shows how to secure access to routes `/basic/**` using the [HTTP Basic Authentication][http_basic] and an in-memory user repository.
- `DigestRouterConfigurer` which shows how to secure access to routes `/digest/**` using the [HTTP Digest Access Authentication][http_digest] and an in-memory user repository.
- `FormRouterConfigurer` which shows how to secure access to routes `/form/**` using an HTML Form authentication and an in-memory user repository.
- `LDAPRouterConfigurer` which shows how to secure access to routes `ldap/**` using an HTML Form authentication and an LDAP directory.
- `SecurityRouterConfigurer` which shows how to secure an application against [CSRF][csrf] attacks and how to configure [Cross-origin resource sharing][cors].

The `AppWebController` specifies the web routes secured in the application and the `AppUserRepository` provides an in-memory user repository with two users (login/password): `reader/password` in group `readers` and `writer/password` in group `writers`.

The Maven build descriptor also defines three build profiles:

- `release` which builds a native application image in a `zip` archive.
- `release-image` which builds a Docker container image of the application in a `tar` archive.
- `install-image` which installs the Docker container image of the application to a local docker daemon.

## Running the application

The application is started using the Inverno Maven plugin as follows:

```plaintext
$ mvn inverno:run
...
2024-09-17 17:18:06,511 INFO  [main] i.i.m.h.s.i.HttpServer - HTTP Server (nio) listening on http://0.0.0.0:8080
2024-09-17 17:18:06,512 INFO  [main] i.i.m.h.s.Server - Module io.inverno.mod.http.server started in 70ms
2024-09-17 17:18:06,512 INFO  [main] i.i.m.w.s.Server - Module io.inverno.mod.web.server started in 71ms
2024-09-17 17:18:06,610 INFO  [main] i.i.e.a.App_web_server_security - Module io.inverno.example.app_web_server_security started in 553ms
2024-09-17 17:18:06,610 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_web_server_security started in 634ms
```

### Basic authentication

HTTP Basic authentication is shown at http://127.0.0.1:8080/basic/hello :

<img src="src/img/basic_auth.png" alt="Inverno Basic Authentication" style="display: block; margin: 2em auto;"/>

<img src="src/img/basic_hello.png" alt="Inverno Basic Authentication success" style="display: block; margin: 2em auto;"/>

Basic credentials are basically the concatenation of login, `":"` and password encoded in base64:

```plaintext
$ curl -i -H 'authorization: basic cmVhZGVyOnBhc3N3b3Jk' http://127.0.0.1:8080/basic/hello
HTTP/1.1 200 OK
content-type: text/plain
access-control-allow-origin: http://127.0.0.1:9090
content-length: 11

Hello basic
```

### Digest authentication

HTTP Digest authentication is shown at http://127.0.0.1:8080/digest/hello:

<img src="src/img/digest_auth.png" alt="Inverno Digest Authentication" style="display: block; margin: 2em auto;"/>

<img src="src/img/digest_hello.png" alt="Inverno Digest Authentication success" style="display: block; margin: 2em auto;"/>

### Form login

Form authentication is shown at http://127.0.0.1:8080/form/hello:

<img src="src/img/form_login.png" alt="Inverno Form login" style="display: block; margin: 2em auto;"/>

<img src="src/img/form_hello.png" alt="Inverno Form success" style="display: block; margin: 2em auto;"/>

A successful form authentication result in the generation of a [JSON Web Token][jwt] returned to the client in a cookie which must be sent to the server to access secured resources:

```plaintext
set-cookie: AUTH-TOKEN=eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJyZWFkZXIiLCJleHAiOjE2NTczNTg4MzN9.cKl5JXQQdt3_6P-QhR1jsN1Lo30CpWLgTSyFFFJTLI4; Path=/form; HttpOnly
```

Here is the decoded JWT Claims set corresponding to the authenticated user:

```json
{
    "iss": "reader",
    "exp": 1657358833
}
```

The user can sign out at http://127.0.0.1:8080/form/logout.

### LDAP

The LDAP example shows authentication against an LDAP server, role-based access control and identification. It requires a running LDAP server which can be started locally using [docker-compose][docker_compose], a `docker-compose.yml` and a ldif file to start and initialize an [OpenLDAP][openldap] server are provided under `openldap/` directory:

```plaintext
openldap $ docker-compose up -d
Creating network "openldap_default" with the default driver
Creating openldap_ldap_1 ... done
```

The LDAP server is initialized with two users: `jsmith/password` in groups `readers` and `writers` and `jdoe/password` in group `readers`.

LDAP authentication is shown at http://127.0.0.1:8080/ldap/hello:

<img src="src/img/ldap_form_login.png" alt="Inverno LDAP Form login" style="display: block; margin: 2em auto;"/>

<img src="src/img/ldap_hello.png" alt="Inverno LDAP Form login success" style="display: block; margin: 2em auto;"/>

A successful form authentication result in the generation of a [JSON Web Signature][jws] containing the actual `LDAPAuthentication` object and returned to the client in a cookie which must be sent to the server to access secured resources:

```plaintext
set-cookie: AUTH-TOKEN=eyJhbGciOiJIUzI1NiJ9.eyJkbiI6ImNuPWpzbWl0aCxvdT11c2VycyxkYz1pbnZlcm5vLGRjPWlvIiwidXNlcm5hbWUiOiJqc21pdGgiLCJhdXRoZW50aWNhdGVkIjp0cnVlLCJncm91cHMiOlsicmVhZGVycyIsIndyaXRlcnMiXX0.rR9Bu4CUebu453rKFKeKC1wPTBfCYE5nYlVHv5KvCSc; Path=/ldap; HttpOnly
```

Here is the decoded JWS payload corresponding to the LDAP authentication:

```json
{
    "dn": "cn=jsmith,ou=users,dc=inverno,dc=io",
    "username": "jsmith",
    "authenticated": true,
    "groups": [
        "readers",
        "writers"
    ]
}
```

The identity of the user is resolved from the LDAP server, it can be displayed at http://127.0.0.1:8080/ldap/identity:

<img src="src/img/ldap_identity.png" alt="Inverno LDAP identity" style="display: block; margin: 2em auto;"/>

Since user `jsmith` is both a reader and a writer, he can access http://127.0.0.1:8080/ldap/read and http://127.0.0.1:8080/ldap/write:

<img src="src/img/ldap_read.png" alt="Inverno LDAP read role" style="display: block; margin: 2em auto;"/>

<img src="src/img/ldap_write.png" alt="Inverno LDAP write role" style="display: block; margin: 2em auto;"/>

However, user `jdoe` which is not a writer can not access http://127.0.0.1:8080/ldap/write:

<img src="src/img/ldap_write_403.png" alt="Inverno LDAP forbidden" style="display: block; margin: 2em auto;"/>

The user can sign out at http://127.0.0.1:8080/ldap/logout.

## Packaging the application

The application can be packaged as a native runtime image by invoking the `release` build profile:

```plaintext
$ mvn install -Prelease
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:package-app (inverno-build-app) @ inverno-example-web-server-security ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project application archives created: zip
[INFO] 
[INFO] --- install:3.1.1:install (default-install) @ inverno-example-web-server-security ---
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-web-server-security/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web-server-security/1.0.0-SNAPSHOT/inverno-example-web-server-security-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-web-server-security/target/inverno-example-web-server-security-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web-server-security/1.0.0-SNAPSHOT/inverno-example-web-server-security-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-web-server-security/target/inverno-example-web-server-security-1.0.0-SNAPSHOT-application_linux_amd64.zip to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web-server-security/1.0.0-SNAPSHOT/inverno-example-web-server-security-1.0.0-SNAPSHOT-application_linux_amd64.zip
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The previous command creates folder `target/inverno-example-web-server-security-1.0.0-SNAPSHOT-application_linux_amd64` containing the Java runtime and the application and installed the corresponding archive to the Maven repository:

```plaintext
$ ./target/inverno-example-web-server-security-1.0.0-SNAPSHOT-application_linux_amd64/bin/example-web-server-security
...
```

A portable docker image of the application can be created as a `tar` archive by invoking the `release-image` build profile:

```plaintext
$ mvn install -Prelease-image
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:package-image (inverno-package-image) @ inverno-example-web-server-security ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project Docker container image TAR archive created
[INFO] 
[INFO] --- install:3.1.1:install (default-install) @ inverno-example-web-server-security ---
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-web-server-security/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web-server-security/1.0.0-SNAPSHOT/inverno-example-web-server-security-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-web-server-security/target/inverno-example-web-server-security-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web-server-security/1.0.0-SNAPSHOT/inverno-example-web-server-security-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-web-server-security/target/inverno-example-web-server-security-1.0.0-SNAPSHOT-container_linux_amd64.tar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web-server-security/1.0.0-SNAPSHOT/inverno-example-web-server-security-1.0.0-SNAPSHOT-container_linux_amd64.tar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The previous command should create archive `target/inverno-example-web-server-security-1.0.0-SNAPSHOT-container_linux_amd64.tar` docker image that can be loaded into docker as follows:

```plaintext
$ docker load --input target/inverno-example-web-server-security-1.0.0-SNAPSHOT-container_linux_amd64.tar
```

The application can be directly deployed to a local docker daemon by invoking the `install-image` build profile:

```plaintext
$ mvn install -Pinstall-image
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:install-image (inverno-install-image) @ inverno-example-web-server-security ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project Docker container image deployed to Docker daemon
[INFO] Project image inverno-example-web-server-security:1.0.0-SNAPSHOT installed to Docker
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The application can then be started in docker as follows:

```plaintext
$ docker run --rm --network host inverno-example-web-server-security:1.0.0-SNAPSHOT 
...
2024-09-17 15:28:05,211 INFO  [main] i.i.m.h.s.i.HttpServer - HTTP Server (nio) listening on http://0.0.0.0:8080
2024-09-17 15:28:05,212 INFO  [main] i.i.m.h.s.Server - Module io.inverno.mod.http.server started in 50ms
2024-09-17 15:28:05,212 INFO  [main] i.i.m.w.s.Server - Module io.inverno.mod.web.server started in 51ms
2024-09-17 15:28:05,300 INFO  [main] i.i.e.a.App_web_server_security - Module io.inverno.example.app_web_server_security started in 496ms
2024-09-17 15:28:05,301 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_web_server_security started in 566ms
```

## Going further

- [HTTP server module documentation][inverno-mod-http-server]
- [Web server module documentation][inverno-mod-web-server]
- [Security module documentation][inverno-mod-security]
- [HTTP Security module documentation][inverno-mod-security-http]
- [JOSE Security module documentation][inverno-mod-security-jose]
- [LDAP Security module documentation][inverno-mod-security-ldap]
- [Inverno distribution documentation][inverno-dist-root]
- [Inverno Maven plugin documentation][inverno-tool-maven-plugin]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]
