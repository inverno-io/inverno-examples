[inverno-mod-http-server]: https://github.com/inverno-io/inverno-mods/blob/master/doc/reference-guide.md#http-server
[inverno-mod-web]: https://github.com/inverno-io/inverno-mods/blob/master/doc/reference-guide.md#web
[inverno-dist-root]: https://github.com/inverno-io/inverno-dist
[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-tool-maven-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-maven-plugin
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[epoll]: https://en.wikipedia.org/wiki/Epoll
[chunked-transfer-encoding]: https://en.wikipedia.org/wiki/Chunked_transfer_encoding
[swagger-ui]: https://swagger.io/tools/swagger-ui/
[open-api]: https://www.openapis.org/

# Inverno Web server example

A sample Inverno application showing how to use the Web server module to create efficient and performant HTTP/1.x and HTTP/2 servers with Web enabled features like request routing, content negociation, automatic payload conversion, static content, WebJars, automatic Open API specifications in SwaggerUI...

The Web server configuration is exposed in the module's configuration `App_webConfiguration`. The configuration in `src/main/resources/configuration.cprops` defines the `https` profile which can be activated to start the server with TLS support. The HTTP server is configured to use [epoll][epoll] when available (ie. on Linux platform) for better performance.

The application defines the `BookResource` Web controller which exposes basic CRUD operations on books as REST resource. The `BookResourceImpl` implementation also demonstrates how to create a server-sent events endpoint or upload files in a `multipart/form-data` request.

An additional Web router configurer `App_webWebRouterConfigurer` shows how to *manually* declare a route serving static resources from local file system or any resource location and how to define routes using content negotiation based on accepted language.

A custom Error Web router `App_webErrorWebRouterConfigurer` is also provided to show how to create custom exception handlers.

Open API specification generation is activated and resulting specifications exposed by the server which also include the Swagger UI to display them.

The Maven build descriptor also defines three build profiles:

- `release` which builds a native application image in a `zip` archive, a docker image in a `tar` archive and installs the docker image of the application in a local docker daemon.
- `release-docker` which builds a docker image in a `tar` archive
- `install-docker` which installs the docker image of the application to a local docker daemon.

## Running the example

The application is started using the Inverno Maven plugin as follows:

```plaintext
$ mvn clean inverno:run
...
2021-04-26 10:54:45,020 INFO  [main] i.w.m.h.s.i.HttpServer - HTTP Server (epoll) listening on http://0.0.0.0:8080
2021-04-26 10:54:45,020 INFO  [main] i.w.m.h.s.Server - Module io.inverno.mod.http.server started in 84ms
2021-04-26 10:54:45,020 INFO  [main] i.w.m.w.Web - Module io.inverno.mod.web started in 84ms
2021-04-26 10:54:45,021 INFO  [main] i.w.e.a.App_web - Module io.inverno.example.app_web started in 288ms
```

TLS can be enabled by activating the `https` configuration:

```plaintext
$ mvn inverno:run -Dinverno.run.arguments="--profile=\\\"https\\\""
...
2021-04-26 10:56:20,975 INFO  [main] i.w.m.h.s.i.HttpServer - HTTP Server (epoll) listening on https://0.0.0.0:8443
2021-04-26 10:56:20,975 INFO  [main] i.w.m.h.s.Server - Module io.inverno.mod.http.server started in 377ms
2021-04-26 10:56:20,976 INFO  [main] i.w.m.w.Web - Module io.inverno.mod.web started in 377ms
2021-04-26 10:56:20,976 INFO  [main] i.w.e.a.App_web - Module io.inverno.example.app_web started in 585ms
```

We can then test the application starting with CRUD operations on book REST resource:

```plaintext
$ curl -i -X POST -H 'content-type: application/json' -d '{"isbn":"978-0132143011","title":"Distributed Systems: Concepts and Design","author":"George Coulouris, Jean Dollimore, Tim Kindberg, Gordon Blair","pages":1080}' http://127.0.0.1:8080/book
HTTP/1.1 201 Created
location: /book/978-0132143011
content-length: 0
```
```plaintext
$ curl -i http://127.0.0.1:8080/book
HTTP/1.1 200 OK
content-type: application/json
transfer-encoding: chunked

[{"isbn":"978-0132143011","title":"Distributed Systems: Concepts and Design","author":"George Coulouris, Jean Dollimore, Tim Kindberg, Gordon Blair","pages":1080}]
```
```plaintext
$ curl -i http://127.0.0.1:8080/book/978-0132143011
HTTP/1.1 200 OK
content-type: application/json
content-length: 161

{"isbn":"978-0132143011","title":"Distributed Systems: Concepts and Design","author":"George Coulouris, Jean Dollimore, Tim Kindberg, Gordon Blair","pages":1080}
```
```plaintext
$ curl -i -X DELETE http://127.0.0.1:8080/book/978-0132143011
HTTP/1.1 200 OK
content-length: 0

```
```plaintext
$ curl -i http://127.0.0.1:8080/book
HTTP/1.1 200 OK
content-type: application/json
transfer-encoding: chunked

[]
```

A book stored in a file (eg. `src/test/resources/book.json`) can be uploaded as follows:

```plaintext
$ curl -iv -Fbook="@src/test/resources/book.json;type=application/json"  http://localhost:8080/book/upload
HTTP/1.1 200 OK
content-type: application/json
transfer-encoding: chunked

[{"message":"Book 978-0132143011 has been stored."}]
```

Book related Server-sent events can be polled as follows:

```plaintext
$ curl -i http://localhost:8080/book/event
HTTP/1.1 200 OK
content-type: text/event-stream;charset=utf-8
transfer-encoding: chunked

1a
id:0
event:bookEvent
data:
21
{"message":"some book event\r\n"}
4



1a
id:1
event:bookEvent
data:
21
{"message":"some book event\r\n"}
4

...
```

With previous command, the server responded with a [chunked transfer encoding][chunked-transfer-encoding] using HTTP/1.x, we could also have queried the server using HTTP/2 over cleartext as follows to produce a better looking response:

```plaintext
$ curl -i --http2 http://localhost:8080/book/event
HTTP/1.1 101 Switching Protocols
connection: upgrade
upgrade: h2c

HTTP/2 200 
content-type: text/event-stream;charset=utf-8

id:0
event:bookEvent
data:{"message":"some book event\r\n"}

id:1
event:bookEvent
data:{"message":"some book event\r\n"}

...
```

The [Swagger UI][swagger-ui] exposing generated [OpenAPI][open-api] specifications is available at `http://127.0.0.1:8080/open-api/`:

<img src="src/img/swaggerUI.png" style="display: block; margin: 2em auto;"/>

Static resources under `web-root/` can be accessed at `http://127.0.0.1:8080/static/`:

```plaintext
$ curl -i http://localhost:8080/static/
HTTP/1.1 200 OK
content-length: 379
content-type: text/html

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Inverno Web example</title>
</head>
<body style="min-width:20em; max-width: 100em; margin: 3em auto;font-family:'Open Sans', Consolas, Helvetica, sans-serif;">
<img src="img/inverno_portable.svg" style="width: 20em;"/>
<hr/>
<p>This is a sample static resource served with the Inverno Web server.</p>
<hr/>
</body>
</html>
```

The `/hello` route is exposed in different languages, content negotiation can be tested by specifying an `accept-language` header:

```plaintext
$ curl -i http://localhost:8080/hello
HTTP/1.1 200 OK
content-type: text/plain
content-length: 8

Hello!
```
```plaintext
$ curl -i -H 'accept-language: fr' http://localhost:8080/hello
HTTP/1.1 200 OK
content-type: text/plain
content-length: 8

Bonjour!
```
```plaintext
$ curl -i -H 'accept-language: it-IT' http://localhost:8080/hello
HTTP/1.1 200 OK
content-type: text/plain
content-length: 8

Saluton!
```

## Packaging the example

The application can be packaged as a native runtime image by invoking the `release` build profile:

```plaintext
$ mvn install -Prelease
...
[INFO] --- inverno-maven-plugin:1.0.0:build-app (inverno-build-app) @ inverno-example-web ---
[INFO] Building application image: /home/jkuhn/Devel/git/frmk/inverno/inverno-examples/inverno-example-web/target/maven-inverno/application_linux_amd64/inverno-example-web-1.0.0-SNAPSHOT...
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] 
[INFO] 
[INFO] --- maven-install-plugin:2.5.2:install (default-install) @ inverno-example-web ---
[INFO] Installing /home/jkuhn/Devel/git/frmk/inverno/inverno-examples/inverno-example-web/target/inverno-example-web-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web/1.0.0-SNAPSHOT/inverno-example-web-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/frmk/inverno/inverno-examples/inverno-example-web/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web/1.0.0-SNAPSHOT/inverno-example-web-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/frmk/inverno/inverno-examples/inverno-example-web/target/inverno-example-web-1.0.0-SNAPSHOT-application_linux_amd64.zip to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web/1.0.0-SNAPSHOT/inverno-example-web-1.0.0-SNAPSHOT-application_linux_amd64.zip
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  16.984 s
[INFO] Finished at: 2021-04-26T14:31:36+02:00
[INFO] ------------------------------------------------------------------------
```

The previous command should create archive `target/inverno-example-web-1.0.0-SNAPSHOT-application_linux_amd64.zip` containing the application and the Java runtime to run it:

```plaintext
$ unzip inverno-example-web-1.0.0-SNAPSHOT-application_linux_amd64.zip
$ ./inverno-example-web-1.0.0-SNAPSHOT/bin/example-web
...
```

A portable docker image of the application can be created as a `tar` archive by invoking the `release-docker` build profile:

```plaintext
$ mvn install -Prelease-docker
...
[INFO] --- inverno-maven-plugin:1.0.0:build-image-tar (inverno-build-image-docker) @ inverno-example-web ---
[INFO] Building project container image...
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] 
[INFO] 
[INFO] --- maven-install-plugin:2.5.2:install (default-install) @ inverno-example-web ---
[INFO] Installing /home/jkuhn/Devel/git/frmk/inverno/inverno-examples/inverno-example-web/target/inverno-example-web-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web/1.0.0-SNAPSHOT/inverno-example-web-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/frmk/inverno/inverno-examples/inverno-example-web/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web/1.0.0-SNAPSHOT/inverno-example-web-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/frmk/inverno/inverno-examples/inverno-example-web/target/inverno-example-web-1.0.0-SNAPSHOT-container_linux_amd64.tar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web/1.0.0-SNAPSHOT/inverno-example-web-1.0.0-SNAPSHOT-container_linux_amd64.tar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  19.606 s
[INFO] Finished at: 2021-04-26T14:38:43+02:00
[INFO] ------------------------------------------------------------------------
```

The previous command should create archive `target/inverno-example-web-1.0.0-SNAPSHOT-container_linux_amd64.tar` docker image that can be loaded into docker as follows:

```plaintext
$ docker load --input target/inverno-example-web-1.0.0-SNAPSHOT-container_linux_amd64.tar
```

The application can be directly deployed to a local docker daemon by invoking the `install-docker` build profile:

```plaintext
$ mvn install -Pinstall-docker
...
[INFO] --- inverno-maven-plugin:1.0.0:build-image-docker (inverno-build-image-docker) @ inverno-example-web ---
[INFO] Building project container image...
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  21.070 s
[INFO] Finished at: 2021-04-26T14:43:42+02:00
[INFO] ------------------------------------------------------------------------
```

The application can then be started in docker as follows:

```plaintext
$ docker run --rm --network host -e LANG=C.UTF-8 inverno-example-web:1.0.0-SNAPSHOT 
...
2021-04-26 12:46:34,284 INFO  [main] i.w.m.h.s.i.HttpServer - HTTP Server (epoll) listening on http://0.0.0.0:8080
2021-04-26 12:46:34,285 INFO  [main] i.w.m.h.s.Server - Module io.inverno.mod.http.server started in 88ms
2021-04-26 12:46:34,285 INFO  [main] i.w.m.w.Web - Module io.inverno.mod.web started in 88ms
2021-04-26 12:46:34,285 INFO  [main] i.w.e.a.App_web - Module io.inverno.example.app_web started in 281ms
```

## Going further

- [HTTP server module documentation][inverno-mod-http-server]
- [Web server module documentation][inverno-mod-web]
- [Inverno distribution documentation][inverno-dist-root]
- [Inverno Maven plugin documentation][inverno-tool-maven-plugin]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]