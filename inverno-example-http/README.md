[inverno-mod-http-server]: https://github.com/inverno-io/inverno-mods/blob/master/doc/reference-guide.md#http-server
[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[epoll]: https://en.wikipedia.org/wiki/Epoll

# Inverno HTTP server example

A sample application showing how to use the HTTP server module to create efficient and performant HTTP/1.x and HTTP/2 servers.

The HTTP server configuration is exposed in the module's configuration `App_httpConfiguration`.

The server is also configured to use [epoll][epoll] when available (ie. on Linux platform) for better performance.

## Running the example

```plaintext
$ mvn inverno:run
2021-04-26 10:15:53,299 INFO  [main] i.w.c.v.Application - Inverno is starting...


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
     ║ Java version        : 11.0.11+9-post-Debian-1deb10u1                                       ║
     ║ Java home           : /usr/lib/jvm/java-11-openjdk-amd64                                   ║
     ║                                                                                            ║
     ║ Application module  : io.inverno.example.app_http                                          ║
     ║ Application class   : io.inverno.example.app_http.Main                                     ║
     ║                                                                                            ║
     ║ Modules             :                                                                      ║
     ║  * ...                                                                                     ║
     ╚════════════════════════════════════════════════════════════════════════════════════════════╝


2021-04-26 10:15:53,303 INFO  [main] i.w.e.a.App_http - Starting Module io.inverno.example.app_http...
2021-04-26 10:15:53,304 INFO  [main] i.w.m.b.Boot - Starting Module io.inverno.mod.boot...
2021-04-26 10:15:53,504 INFO  [main] i.w.m.b.Boot - Module io.inverno.mod.boot started in 200ms
2021-04-26 10:15:53,504 INFO  [main] i.w.m.h.s.Server - Starting Module io.inverno.mod.http.server...
2021-04-26 10:15:53,504 INFO  [main] i.w.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2021-04-26 10:15:53,508 INFO  [main] i.w.m.h.b.Base - Module io.inverno.mod.http.base started in 4ms
2021-04-26 10:15:53,915 INFO  [main] i.w.m.h.s.i.HttpServer - HTTP Server (epoll) listening on https://0.0.0.0:8443
2021-04-26 10:15:53,916 INFO  [main] i.w.m.h.s.Server - Module io.inverno.mod.http.server started in 411ms
2021-04-26 10:15:53,916 INFO  [main] i.w.e.a.App_http - Module io.inverno.example.app_http started in 613ms
```

We can test the application using `curl`:

```plaintext
$ curl --insecure -i https://127.0.0.1:8443
HTTP/2 200 
content-length: 16

Hello from main!
```

## Going further

- [HTTP server module documentation][inverno-mod-http-server]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]