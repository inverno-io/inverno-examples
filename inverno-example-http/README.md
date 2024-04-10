[inverno-mod-http-server]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-http-server/
[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[epoll]: https://en.wikipedia.org/wiki/Epoll
[graalvm]: https://www.graalvm.org/

# Inverno HTTP server example

A sample application showing how to use the HTTP server module to create efficient and performant HTTP/1.x and HTTP/2 servers.

The HTTP server configuration is exposed in the module's configuration `App_httpConfiguration`.

The server is configured to use [epoll][epoll] when available (ie. on Linux platform) for better performance and to request TLS client authentication.

## Running the application

The application is started using the Inverno Maven plugin as follows:

```plaintext
$ mvn inverno:run
...
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Running project io.inverno.example.app_http@1.0.0-SNAPSHOT...
2024-04-09 14:37:06,413 INFO  [main] i.i.c.v.Application - Inverno is starting...


     ╔════════════════════════════════════════════════════════════════════════════════════════════╗
     ║                      , ~~ ,                                                                ║
     ║                  , '   /\   ' ,                                                            ║
     ║                 , __   \/   __ ,      _                                                    ║
     ║                ,  \_\_\/\/_/_/  ,    | |  ___  _    _  ___   __  ___   ___                 ║
     ║                ,    _\_\/_/_    ,    | | / _ \\ \  / // _ \ / _|/ _ \ / _ \                ║
     ║                ,   __\_/\_\__   ,    | || | | |\ \/ /|  __/| | | | | | |_| |               ║
     ║                 , /_/ /\/\ \_\ ,     |_||_| |_| \__/  \___||_| |_| |_|\___/                ║
     ║                  ,     /\     ,                                                            ║
     ║                    ,   \/   ,                                  -- ${VERSION_INVERNO_CORE} --                 ║
     ║                      ' -- '                                                                ║
     ╠════════════════════════════════════════════════════════════════════════════════════════════╣
     ║ Java runtime        : OpenJDK Runtime Environment                                          ║
     ║ Java version        : 21.0.2+13-58                                                         ║
     ║ Java home           : /home/jkuhn/Devel/jdk/jdk-21.0.2                                     ║
     ║                                                                                            ║
     ║ Application module  : io.inverno.example.app_http                                          ║
     ║ Application version : 1.0.0-SNAPSHOT                                                       ║
     ║ Application class   : io.inverno.example.app_http.Main                                     ║
     ║                                                                                            ║
     ║ Modules             :                                                                      ║
     ║  ...                                                                                       ║
     ╚════════════════════════════════════════════════════════════════════════════════════════════╝


2024-04-09 14:37:06,422 INFO  [main] i.i.e.a.App_http - Starting Module io.inverno.example.app_http...
2024-04-09 14:37:06,423 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2024-04-09 14:37:06,635 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 212ms
2024-04-09 14:37:06,636 INFO  [main] i.i.m.h.s.Server - Starting Module io.inverno.mod.http.server...
2024-04-09 14:37:06,636 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-04-09 14:37:06,640 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 4ms
2024-04-09 14:37:06,963 INFO  [main] i.i.m.h.s.i.HttpServer - HTTP Server (epoll) listening on https://0.0.0.0:8443
2024-04-09 14:37:06,963 INFO  [main] i.i.m.h.s.Server - Module io.inverno.mod.http.server started in 327ms
2024-04-09 14:37:06,963 INFO  [main] i.i.e.a.App_http - Module io.inverno.example.app_http started in 548ms
2024-04-09 14:37:06,964 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_http started in 594ms
```

We can test the application using `curl`:

```plaintext
$ curl --insecure -i https://127.0.0.1:8443
HTTP/2 200 
content-length: 16

Hello from main!
```

In above example, `curl` client did not authenticate to the server and blindly trusted the server certificate using the `--insecure` option. It can authenticate and verify server certificate with the following command:

```plaintext
$ curl -i --cert src/main/resources/client-certificate.pem --key src/main/resources/client-key.pem --cacert src/main/resources/server-certificate.pem https://127.0.0.1:8443
HTTP/1.1 200 OK
content-length: 26

Hello Http Example Client!
```

In this example, `Http Example Client` is the common name (CN) extracted from the client certificate received by the server.

> The `src/main/resources` folder contains both server and client keystores (`server-keystore.jks` and `client-keystore.jks` respectively) whose certificates have been added to client and server truststores (`client-truststore.jks` and `server-truststore.jks` respectively). The server is configured to request client authentication (`tls_client_auth=true`) and to trust client certificates contained in the server trutstore.

## Building a native image

Using [GraalVM][graalvm], you can also build a native image of the application with the following command:

```plaintext
> mvn clean package -Pnative
```

You can then run the native application:

```plaintext
$ ./target/inverno-example-http 
2024-04-09 14:46:35,350 INFO  [main] i.i.c.v.Application - Inverno is starting...


     ╔════════════════════════════════════════════════════════════════════════════════════════════╗
     ║                      , ~~ ,                                                                ║
     ║                  , '   /\   ' ,                                                            ║
     ║                 , __   \/   __ ,      _                                                    ║
     ║                ,  \_\_\/\/_/_/  ,    | |  ___  _    _  ___   __  ___   ___                 ║
     ║                ,    _\_\/_/_    ,    | | / _ \\ \  / // _ \ / _|/ _ \ / _ \                ║
     ║                ,   __\_/\_\__   ,    | || | | |\ \/ /|  __/| | | | | | |_| |               ║
     ║                 , /_/ /\/\ \_\ ,     |_||_| |_| \__/  \___||_| |_| |_|\___/                ║
     ║                  ,     /\     ,                                                            ║
     ║                    ,   \/   ,                                   << n/a >>                  ║
     ║                      ' -- '                                                                ║
     ╠════════════════════════════════════════════════════════════════════════════════════════════╣
     ║ Java runtime        : GraalVM Runtime Environment                                          ║
     ║ Java version        : 21.0.2+13-LTS-jvmci-23.1-b30                                         ║
     ║ Java home           :                                                                      ║
     ╚════════════════════════════════════════════════════════════════════════════════════════════╝


2024-04-09 14:46:35,351 INFO  [main] i.i.e.a.App_http - Starting Module io.inverno.example.app_http...
2024-04-09 14:46:35,351 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2024-04-09 14:46:35,356 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 5ms
2024-04-09 14:46:35,356 INFO  [main] i.i.m.h.s.Server - Starting Module io.inverno.mod.http.server...
2024-04-09 14:46:35,356 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-04-09 14:46:35,356 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 0ms
2024-04-09 14:46:35,402 INFO  [main] i.i.m.h.s.i.HttpServer - HTTP Server (epoll) listening on https://0.0.0.0:8443
2024-04-09 14:46:35,402 INFO  [main] i.i.m.h.s.Server - Module io.inverno.mod.http.server started in 45ms
2024-04-09 14:46:35,402 INFO  [main] i.i.e.a.App_http - Module io.inverno.example.app_http started in 51ms
2024-04-09 14:46:35,402 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_http started in 51ms
```

> Note that for the native image to work, [logback][logback] must be used as logging manager since log4j doesn't support native build (see https://issues.apache.org/jira/browse/LOG4J2-2649).

## Going further

- [HTTP server module documentation][inverno-mod-http-server]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]