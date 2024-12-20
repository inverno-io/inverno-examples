[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-dist-root]: https://github.com/inverno-io/inverno-dist
[inverno-tool-maven-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-maven-plugin
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[inverno-mod-http-server]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-http-server/

[epoll]: https://en.wikipedia.org/wiki/Epoll
[graalvm]: https://www.graalvm.org/
[logback]: https://logback.qos.ch/

# Inverno HTTP server WebSocket example

A sample application showing how to use the HTTP server module to create a WebSocket server.

The HTTP server configuration is exposed in the module's configuration `AppConfiguration`.

The server is also configured to use [epoll][epoll] when available (i.e. on Linux platform) for better performance.

## Running the application

The application is started using the Inverno Maven plugin as follows:

```plaintext
$ mvn inverno:run
...
2024-04-09 14:52:56,549 INFO  [main] i.i.c.v.Application - Inverno is starting...


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
     ║ Application module  : io.inverno.example.app_http_server_websocket                         ║
     ║ Application version : 1.0.0-SNAPSHOT                                                       ║
     ║ Application class   : io.inverno.example.app_http_server_websocket.Main                    ║
     ║                                                                                            ║
     ║ Modules             :                                                                      ║
     ║  * ...                                                                                     ║
     ╚════════════════════════════════════════════════════════════════════════════════════════════╝


2024-09-17 14:51:24,144 INFO  [main] i.i.e.a.App_http_server_websocket - Starting Module io.inverno.example.app_http_server_websocket...
2024-09-17 14:51:24,144 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2024-09-17 14:51:24,399 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 254ms
2024-09-17 14:51:24,399 INFO  [main] i.i.m.h.s.Server - Starting Module io.inverno.mod.http.server...
2024-09-17 14:51:24,399 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-09-17 14:51:24,404 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 4ms
2024-09-17 14:51:24,483 INFO  [main] i.i.m.h.s.i.HttpServer - HTTP Server (epoll) listening on http://0.0.0.0:8080
2024-09-17 14:51:24,484 INFO  [main] i.i.m.h.s.Server - Module io.inverno.mod.http.server started in 84ms
2024-09-17 14:51:24,484 INFO  [main] i.i.e.a.App_http_server_websocket - Module io.inverno.example.app_http_server_websocket started in 343ms
2024-09-17 14:51:24,485 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_http_server_websocket started in 392ms
```

The application is accessible at the following location: http://127.0.0.1:8080, it shows a simplistic chat application backed by a WebSocket:

<img src="src/img/inverno_http_chat.png" alt="Inverno HTTP Chat application" style="display: block; margin: 2em auto;"/>

## Building a native image

Using [GraalVM][graalvm], you can also build a native image of the application with the following command:

```plaintext
> mvn clean package -Pnative
```

You can then run the native application:

```plaintext
> ./target/example-http-server-websocket
2024-09-17 14:59:09,168 INFO  [main] i.i.c.v.Application - Inverno is starting...


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


2024-09-17 14:59:09,168 INFO  [main] i.i.e.a.App_http_server_websocket - Starting Module io.inverno.example.app_http_server_websocket...
2024-09-17 14:59:09,168 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2024-09-17 14:59:09,174 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 6ms
2024-09-17 14:59:09,174 INFO  [main] i.i.m.h.s.Server - Starting Module io.inverno.mod.http.server...
2024-09-17 14:59:09,174 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-09-17 14:59:09,174 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 0ms
2024-09-17 14:59:09,176 INFO  [main] i.i.m.h.s.i.HttpServer - HTTP Server (epoll) listening on http://0.0.0.0:8080
2024-09-17 14:59:09,176 INFO  [main] i.i.m.h.s.Server - Module io.inverno.mod.http.server started in 1ms
2024-09-17 14:59:09,176 INFO  [main] i.i.e.a.App_http_server_websocket - Module io.inverno.example.app_http_server_websocket started in 7ms
2024-09-17 14:59:09,176 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_http_server_websocket started in 8ms
```

> Note that for the native image to work, [logback][logback] must be used as logging manager since log4j doesn't support native build (see https://issues.apache.org/jira/browse/LOG4J2-2649).

## Going further

- [HTTP server module documentation][inverno-mod-http-server]
- [Inverno distribution documentation][inverno-dist-root]
- [Inverno Maven plugin documentation][inverno-tool-maven-plugin]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]
