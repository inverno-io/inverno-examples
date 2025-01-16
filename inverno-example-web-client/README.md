[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-dist-root]: https://github.com/inverno-io/inverno-dist
[inverno-tool-maven-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-maven-plugin
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[inverno-mod-discovery]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery/
[inverno-mod-discovery-http]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery-http/
[inverno-mod-discovery-http-meta]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery-http-meta/
[inverno-mod-http-client]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-http-client/
[inverno-mod-web-client]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-web-client/
[inverno-examples-discovery-http]: ../inverno-example-discovery-http
[inverno-examples-discovery-http-testserver]: ../inverno-example-discovery-http-testserver
[inverno-examples-web-server]: ../inverno-example-web-server

[epoll]: https://en.wikipedia.org/wiki/Epoll
[graalvm]: https://www.graalvm.org/
[logback]: https://logback.qos.ch/
[picocli]: https://picocli.info/

# Inverno Web client example

A sample application showing how to use the Web client module to create programmatic or declarative HTTP clients supporting automatic content encoding/decoding and service discovery.

The example provides two command line applications: a book client application for consuming the book resource exposed in the [Web server example application][inverno-examples-web-server] and a universal Web client.

The Web client and discovery configurations are exposed in the module's configuration `AppConfiguration`, a base configuration can be specified in a `configuration.cprops` file.

The application uses [Picocli][picocli] to parse the command line, and it is configured to use [epoll][epoll] when available (i.e. on Linux platform) for better performance.

The Maven build descriptor also defines a `release` profile which builds a native application image in a `zip` archive.

## Running the application

### Book Web client

The book client application can be started using the Inverno Maven plugin as follows:

```plaintext
$ mvn inverno:run -Dinverno.exec.mainClass="io.inverno.example.app_web_client.book.Main" -Dinverno.run.arguments="help"
...
Usage:  [COMMAND]
Inverno Book resource commands
Commands:
  help    Display help information about the specified command.
  create  Creates a book resource
  update  Updates a book resource
  list    List book resources
  get     Get a book resource
  delete  Delete a book resource
...
```

But it is more convenient to run it with the native launcher generated when packaging the application (see [Packaging the application](#packaging-the-application)). As described above, it exposes 5 commands to create, update, list, get or delete book resources exposed in the [Web server example application][inverno-examples-web-server] as illustrated below:

```plaintext
$ ./example-book-client create --isbn="978-0132143011" --title="Distributed Systems: Concepts and Design" --author="George Coulouris, Jean Dollimore, Tim Kindberg, Gordon Blair" --pages=1080
1 book created
$ ./example-book-client list
1 book(s)
 * Book{isbn='978-0132143011', title='Distributed Systems: Concepts and Design', author='George Coulouris, Jean Dollimore, Tim Kindberg, Gordon Blair', pages=1080}
$ ./example-book-client update --isbn=978-0132143011 --title="Distributed Systems Concepts and Design" --author="George Coulouris & Jean Dollimore & Tim Kindberg & Gordon Blair" --pages=1234
1 book updated
$ ./example-book-client get 978-0132143011
Book{isbn='978-0132143011', title='Distributed Systems Concepts and Design', author='George Coulouris & Jean Dollimore & Tim Kindberg & Gordon Blair', pages=1234}
$ ./example-book-client delete 978-0132143011
1 book deleted
```

### Universal Web client

The universal Web client application can be started using the Inverno Maven plugin as follows:

```plaintext
$ mvn inverno:run -Dinverno.exec.mainClass="io.inverno.example.app_web_client.Main" -Dinverno.run.arguments="-h"
...
Usage: example-web-client [-hV] [-a=<accept>] [-ae=<acceptEncoding>]
                          [-al=<acceptLanguage>] [-auth=<authority>]
                          [-ct=<contentType>] [-m=<method>]
                          [-H=<String=String>]... [-d=<data> | [-pn=<name>
                          [-pv=<value>]]... | [-fn=<name> [-ff=<filename>]
                          [-fH=<String=String>]... [-ft=<type>]
                          [-fd=<data>]]...] uri
Inverno Web Client example
      uri                 The resource URI
  -a, --accept=<accept>   Set the accept header
      -ae, --accept-encoding=<acceptEncoding>
                          Set the accept-encoding header
      -al, --accept-language=<acceptLanguage>
                          Set the accept-language header
      -auth, --authority=<authority>
                          Set the request authority
      -ct, --content-type=<contentType>
                          Set the content-type header
  -d, --data=<data>       Data to send in the request
                          Use '@' to specify a path path to a local file
  -h, --help              Show this help message and exit.
  -H, --header=<String=String>
                          Header to include in the request when sending HTTP to
                            a server
  -m, --method=<method>   Set the HTTP request method Header
  -V, --version           Print version information and exit.
URL encoded Form
      -pn, --parameter-name=<name>
                          Set the parameter name
      -pv, --parameter-value=<value>
                          Set the parameter value
Multipart Form
      -fd, --form-data=<data>
                          Data to send in the form part
                          Use '@' to specify a path path to a local file
      -ff, --form-filename=<filename>
                          Set the form part filename
                          If a local file path is specified in the data
                            parameter, this defaults to the name of the local
                            file name
      -fH, --form-header=<String=String>
                          Header to include in the form part
      -fn, --form-name=<name>
                          Set the form part name
      -ft, --form-type=<type>
                          Set the form part content-type
...
```

But it is more convenient to run it with the native launcher generated when packaging the application (see [Packaging the application](#packaging-the-application)). As described above, it can be used to send any kind of HTTP request to any HTTP server.

It is for instance possible to consume the book resource as in previous example:

```plaintext
$ ./example-web-client -m POST -ct 'application/json' -d '{"isbn":"978-0132143011","title":"Distributed Systems: Concepts and Design","author":"George Coulouris, Jean Dollimore, Tim Kindberg, Gordon Blair","pages":1080}' http://127.0.0.1:8080/book
> POST /book h2
> content-type: application/json
> user-agent: Inverno/1.12.0-SNAPSHOT
> host: 127.0.0.1:8080
> upgrade: h2c
> http2-settings: AAEAABAAAAIAAAABAAN_____AAQAAP__AAUAAEAAAAZ_____
> connection: upgrade,http2-settings
> content-length: 161

{"isbn":"978-0132143011","title":"Distributed Systems: Concepts and Design","author":"George Coulouris, Jean Dollimore, Tim Kindberg, Gordon Blair","pages":1080}
--------------------------------------------------------------------------------
< h2 201 Created
< :status: 201
< location: /book/978-0132143011
< content-length: 0


$ ./example-web-client http://127.0.0.1:8080/book
> GET /book h2
> user-agent: Inverno/1.12.0-SNAPSHOT
> host: 127.0.0.1:8080
> upgrade: h2c
> http2-settings: AAEAABAAAAIAAAABAAN_____AAQAAP__AAUAAEAAAAZ_____
> connection: upgrade,http2-settings


--------------------------------------------------------------------------------
< h2 200 OK
< :status: 200
< content-type: application/json

[{"isbn":"978-0132143011","title":"Distributed Systems: Concepts and Design","author":"George Coulouris, Jean Dollimore, Tim Kindberg, Gordon Blair","pages":1080}]
$ ./example-web-client -m PUT -ct 'application/json' -d '{"isbn":"978-0132143011","title":"Distributed Systems - Concepts and Design","author":"George Coulouris & Jean Dollimore & Tim Kindberg & Gordon Blair","pages":1234}' http://127.0.0.1:8080/book/978-0132143011
> PUT /book/978-0132143011 h2
> content-type: application/json
> user-agent: Inverno/1.12.0-SNAPSHOT
> host: 127.0.0.1:8080
> upgrade: h2c
> http2-settings: AAEAABAAAAIAAAABAAN_____AAQAAP__AAUAAEAAAAZ_____
> connection: upgrade,http2-settings
> content-length: 165

{"isbn":"978-0132143011","title":"Distributed Systems - Concepts and Design","author":"George Coulouris & Jean Dollimore & Tim Kindberg & Gordon Blair","pages":1234}
--------------------------------------------------------------------------------
< h2 200 OK
< :status: 200
< content-length: 0


$ ./example-web-client http://127.0.0.1:8080/book/978-0132143011
> GET /book/978-0132143011 h2
> user-agent: Inverno/1.12.0-SNAPSHOT
> host: 127.0.0.1:8080
> upgrade: h2c
> http2-settings: AAEAABAAAAIAAAABAAN_____AAQAAP__AAUAAEAAAAZ_____
> connection: upgrade,http2-settings


--------------------------------------------------------------------------------
< h2 200 OK
< :status: 200
< content-type: application/json
< content-length: 165

{"isbn":"978-0132143011","title":"Distributed Systems - Concepts and Design","author":"George Coulouris & Jean Dollimore & Tim Kindberg & Gordon Blair","pages":1234}
$ ./example-web-client -m DELETE http://127.0.0.1:8080/book/978-0132143011
> DELETE /book/978-0132143011 h2
> user-agent: Inverno/1.12.0-SNAPSHOT
> host: 127.0.0.1:8080
> upgrade: h2c
> http2-settings: AAEAABAAAAIAAAABAAN_____AAQAAP__AAUAAEAAAAZ_____
> connection: upgrade,http2-settings


--------------------------------------------------------------------------------
< h2 200 OK
< :status: 200
< content-length: 0


```

The [Web client module][inverno-mod-web-client] embeds a composite HTTP discovery service which composes the HTTP Discovery services exposed in the module namely the `dnsHttpDiscoveryService` and `configurationHttpMetaDiscoveryService` respectively provided by the [Discovery HTTP module][inverno-mod-discovery-http] and the [Discovery HTTP Metadata module][inverno-mod-discovery-http-meta]. 

The application can then connect to servers whose Inet Socket Address is resolved with DNS:

```plaintext
$ ./example-web-client -a application/json https://timeapi.io/api/time/current/zone?timeZone=Europe/Paris
* Server certificate:
   subject: CN=timeapi.io
   start date: Tue Aug 13 02:00:00 CEST 2024
   expire date: Fri Aug 15 01:59:59 CEST 2025
   subjectAltName: [2, timeapi.io], [2, www.timeapi.io]
   issuer: CN=Sectigo RSA Domain Validation Secure Server CA, O=Sectigo Limited, L=Salford, ST=Greater Manchester, C=GB

> GET /api/time/current/zone?timeZone=Europe/Paris HTTP/1.1
> accept: application/json
> user-agent: Inverno/1.12.0-SNAPSHOT
> host: timeapi.io


--------------------------------------------------------------------------------
< HTTP/1.1 200 OK
< Server: nginx/1.27.0
< Date: Wed, 18 Sep 2024 08:23:05 GMT
< Content-Type: application/json; charset=utf-8
< Transfer-Encoding: chunked
< Connection: keep-alive

{"year":2024,"month":9,"day":18,"hour":10,"minute":23,"seconds":5,"milliSeconds":237,"dateTime":"2024-09-18T10:23:05.2377486","date":"09/18/2024","time":"10:23","timeZone":"Europe/Paris","dayOfWeek":"Wednesday","dstActive":true}
```

The client can also resolve configuration URIs as the ones used in [Discovery HTTP example application][inverno-examples-discovery-http] and defined in `src/main/resources/configuration.cprops`. Assuming we have started three [test server instances][inverno-examples-discovery-http-testserver] we can run the following:

```plaintext
$ ./example-web-client conf://singleDestinationWithConfig/path/to/resource
> GET /path/to/resource h2
> user-agent: Discovery example client
> host: 127.0.0.1:8080
> upgrade: h2c
> http2-settings: AAEAABAAAAIAAAABAAN_____AAQAAP__AAUAAEAAAAZ_____
> connection: upgrade,http2-settings


--------------------------------------------------------------------------------
< h2 200 OK
< :status: 200
< content-type: application/json
< content-length: 358

{"localAddress":"/127.0.0.1:8080","remoteAddress":"/127.0.0.1:53124","method":"GET","path":"/path/to/resource","authority":"127.0.0.1:8080","headers":{"content-length":"0","upgrade":"h2c",":method":"GET",":scheme":"http","connection":"upgrade,http2-settings",":path":"/path/to/resource",":authority":"127.0.0.1:8080","user-agent":"Discovery example client"}}
$ ./example-web-client conf://multiDestinationRoutingPath/path/to/resource
> GET /path/to/resource h2
> user-agent: Inverno/1.12.0-SNAPSHOT
> host: 127.0.0.1:8080
> upgrade: h2c
> http2-settings: AAEAABAAAAIAAAABAAN_____AAQAAP__AAUAAEAAAAZ_____
> connection: upgrade,http2-settings


--------------------------------------------------------------------------------
< h2 200 OK
< :status: 200
< content-type: application/json
< content-length: 357

{"localAddress":"/127.0.0.1:8080","remoteAddress":"/127.0.0.1:42572","method":"GET","path":"/path/to/resource","authority":"127.0.0.1:8080","headers":{"content-length":"0","upgrade":"h2c",":method":"GET",":scheme":"http","connection":"upgrade,http2-settings",":path":"/path/to/resource",":authority":"127.0.0.1:8080","user-agent":"Inverno/1.12.0-SNAPSHOT"}}
$ ./example-web-client --authority=service-2 conf://multiDestinationRoutingAuthority/path/to/resource
> GET /path/to/resource h2
> user-agent: Inverno/1.12.0-SNAPSHOT
> host: service-2
> upgrade: h2c
> http2-settings: AAEAABAAAAIAAAABAAN_____AAQAAP__AAUAAEAAAAZ_____
> connection: upgrade,http2-settings


--------------------------------------------------------------------------------
< h2 200 OK
< :status: 200
< content-type: application/json
< content-length: 347

{"localAddress":"/127.0.0.1:8082","remoteAddress":"/127.0.0.1:55948","method":"GET","path":"/path/to/resource","authority":"service-2","headers":{"content-length":"0","upgrade":"h2c",":method":"GET",":scheme":"http","connection":"upgrade,http2-settings",":path":"/path/to/resource",":authority":"service-2","user-agent":"Inverno/1.12.0-SNAPSHOT"}}
$ ./example-web-client conf://multiDestinationRewritePath/path/to/resource
> GET /path/to/resource h2
> user-agent: Inverno/1.12.0-SNAPSHOT
> host: 127.0.0.1:8080
> upgrade: h2c
> http2-settings: AAEAABAAAAIAAAABAAN_____AAQAAP__AAUAAEAAAAZ_____
> connection: upgrade,http2-settings


--------------------------------------------------------------------------------
< h2 200 OK
< :status: 200
< content-type: application/json
< content-length: 357

{"localAddress":"/127.0.0.1:8080","remoteAddress":"/127.0.0.1:54398","method":"GET","path":"/path/to/resource","authority":"127.0.0.1:8080","headers":{"content-length":"0","upgrade":"h2c",":method":"GET",":scheme":"http","connection":"upgrade,http2-settings",":path":"/path/to/resource",":authority":"127.0.0.1:8080","user-agent":"Inverno/1.12.0-SNAPSHOT"}}
$ ./example-web-client conf://multiDestinationRewritePath/v1/path/to/resource
> GET /path/to/resource h2
> user-agent: Inverno/1.12.0-SNAPSHOT
> host: 127.0.0.1:8081
> upgrade: h2c
> http2-settings: AAEAABAAAAIAAAABAAN_____AAQAAP__AAUAAEAAAAZ_____
> connection: upgrade,http2-settings


--------------------------------------------------------------------------------
< h2 200 OK
< :status: 200
< content-type: application/json
< content-length: 357

{"localAddress":"/127.0.0.1:8081","remoteAddress":"/127.0.0.1:38722","method":"GET","path":"/path/to/resource","authority":"127.0.0.1:8081","headers":{"content-length":"0","upgrade":"h2c",":method":"GET",":scheme":"http","connection":"upgrade,http2-settings",":path":"/path/to/resource",":authority":"127.0.0.1:8081","user-agent":"Inverno/1.12.0-SNAPSHOT"}}
$ ./example-web-client conf://multiDestinationSetHeaders/path/to/resource
> GET /path/to/resource h2
> route-header: abc
> user-agent: Inverno/1.12.0-SNAPSHOT
> host: 127.0.0.1:8080
> upgrade: h2c
> http2-settings: AAEAABAAAAIAAAABAAN_____AAQAAP__AAUAAEAAAAZ_____
> connection: upgrade,http2-settings


--------------------------------------------------------------------------------
< h2 200 OK
< :status: 200
< content-type: application/json
< content-length: 378

{"localAddress":"/127.0.0.1:8080","remoteAddress":"/127.0.0.1:42136","method":"GET","path":"/path/to/resource","authority":"127.0.0.1:8080","headers":{"content-length":"0","upgrade":"h2c",":method":"GET",":scheme":"http","connection":"upgrade,http2-settings",":path":"/path/to/resource","route-header":"abc",":authority":"127.0.0.1:8080","user-agent":"Inverno/1.12.0-SNAPSHOT"}}
```

## Packaging the application

The application can be packaged as a native runtime image by invoking the `release` build profile:

```plaintext
$ mvn install -Prelease
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:package-app (inverno-package-app) @ inverno-example-web-client ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project application archives created: zip
[INFO] 
[INFO] --- install:3.1.1:install (default-install) @ inverno-example-web-client ---
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-web-client/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web-client/1.0.0-SNAPSHOT/inverno-example-web-client-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-web-client/target/inverno-example-web-client-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web-client/1.0.0-SNAPSHOT/inverno-example-web-client-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-web-client/target/inverno-example-web-client-1.0.0-SNAPSHOT-application_linux_amd64.zip to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-web-client/1.0.0-SNAPSHOT/inverno-example-web-client-1.0.0-SNAPSHOT-application_linux_amd64.zip
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The previous command creates folder `target/inverno-example-web-client-1.0.0-SNAPSHOT-application_linux_amd64` containing the Java runtime and the application and installed the corresponding archive to the Maven repository.

```plaintext
$ ./target/inverno-example-web-client-1.0.0-SNAPSHOT-application_linux_amd64/bin/example-book-client help
Usage:  [COMMAND]
Book commands
Commands:
  help    Display help information about the specified command.
  create  Creates a book resource
  update  Updates a book resource
  list    List book resources
  get     Get a book resource
  delete  Delete a book resource

```

```plaintext
$ ./target/inverno-example-web-client-1.0.0-SNAPSHOT-application_linux_amd64/bin/example-web-client -h
Usage: example-web-client [-hV] [-a=<accept>] [-ae=<acceptEncoding>]
                          [-al=<acceptLanguage>] [-auth=<authority>]
                          [-ct=<contentType>] [-m=<method>]
                          [-H=<String=String>]... [-d=<data> | [-pn=<name>
                          [-pv=<value>]]... | [-fn=<name> [-ff=<filename>]
                          [-fH=<String=String>]... [-ft=<type>]
                          [-fd=<data>]]...] uri
Inverno Web Client example
      uri                 The resource URI
  -a, --accept=<accept>   Set the accept header
      -ae, --accept-encoding=<acceptEncoding>
                          Set the accept-encoding header
      -al, --accept-language=<acceptLanguage>
                          Set the accept-language header
      -auth, --authority=<authority>
                          Set the request authority
      -ct, --content-type=<contentType>
                          Set the content-type header
  -d, --data=<data>       Data to send in the request
                          Use '@' to specify a path path to a local file
  -h, --help              Show this help message and exit.
  -H, --header=<String=String>
                          Header to include in the request when sending HTTP to
                            a server
  -m, --method=<method>   Set the HTTP request method Header
  -V, --version           Print version information and exit.
URL encoded Form
      -pn, --parameter-name=<name>
                          Set the parameter name
      -pv, --parameter-value=<value>
                          Set the parameter value
Multipart Form
      -fd, --form-data=<data>
                          Data to send in the form part
                          Use '@' to specify a path path to a local file
      -ff, --form-filename=<filename>
                          Set the form part filename
                          If a local file path is specified in the data
                            parameter, this defaults to the name of the local
                            file name
      -fH, --form-header=<String=String>
                          Header to include in the form part
      -fn, --form-name=<name>
                          Set the form part name
      -ft, --form-type=<type>
                          Set the form part content-type

```

## Building a native image

Using [GraalVM][graalvm], you can also build a native image of the universal Web client application with the following command:

```plaintext
$ mvn clean package -Pnative
```

You can then run the native application:

```plaintext
$ ./target/inverno-example-web-client -h
Usage: example-web-client [-hV] [-a=<accept>] [-ae=<acceptEncoding>]
                          [-al=<acceptLanguage>] [-auth=<authority>]
                          [-ct=<contentType>] [-m=<method>]
                          [-H=<String=String>]... [-d=<data> | [-pn=<name>
                          [-pv=<value>]]... | [-fn=<name> [-ff=<filename>]
                          [-fH=<String=String>]... [-ft=<type>]
                          [-fd=<data>]]...] uri
Inverno Web Client example
      uri                 The resource URI
  -a, --accept=<accept>   Set the accept header
      -ae, --accept-encoding=<acceptEncoding>
                          Set the accept-encoding header
      -al, --accept-language=<acceptLanguage>
                          Set the accept-language header
      -auth, --authority=<authority>
                          Set the request authority
      -ct, --content-type=<contentType>
                          Set the content-type header
  -d, --data=<data>       Data to send in the request
                          Use '@' to specify a path path to a local file
  -h, --help              Show this help message and exit.
  -H, --header=<String=String>
                          Header to include in the request when sending HTTP to
                            a server
  -m, --method=<method>   Set the HTTP request method Header
  -V, --version           Print version information and exit.
URL encoded Form
      -pn, --parameter-name=<name>
                          Set the parameter name
      -pv, --parameter-value=<value>
                          Set the parameter value
Multipart Form
      -fd, --form-data=<data>
                          Data to send in the form part
                          Use '@' to specify a path path to a local file
      -ff, --form-filename=<filename>
                          Set the form part filename
                          If a local file path is specified in the data
                            parameter, this defaults to the name of the local
                            file name
      -fH, --form-header=<String=String>
                          Header to include in the form part
      -fn, --form-name=<name>
                          Set the form part name
      -ft, --form-type=<type>
                          Set the form part content-type
```

> Note that for the native image to work, [logback][logback] must be used as logging manager since log4j doesn't support native build (see https://issues.apache.org/jira/browse/LOG4J2-2649).

## Going further

- [HTTP client module documentation][inverno-mod-http-client]
- [Web client module documentation][inverno-mod-web-client]
- [Discovery API module documentation][inverno-mod-discovery]
- [Discovery HTTP module documentation][inverno-mod-discovery-http]
- [Discovery HTTP Metadata module documentation][inverno-mod-discovery-http-meta]
- [Inverno distribution documentation][inverno-dist-root]
- [Inverno Maven plugin documentation][inverno-tool-maven-plugin]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]