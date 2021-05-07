[winter-mod-http-server]: https://github.com/winterframework-io/winter-mods/blob/master/doc/reference-guide.md#http-server
[winter-root-doc]: https://github.com/winterframework-io/winter/blob/master/doc/reference-guide.md
[javadoc]: http://tbd

[epoll]: https://en.wikipedia.org/wiki/Epoll

# Winter HTTP server example

A sample application showing how to use the HTTP server module to create efficient and performant HTTP/1.x and HTTP/2 servers.

The HTTP server configuration is exposed in the module's configuration `App_httpConfiguration`.

The server is also configured to use [epoll][epoll] when available (ie. on Linux platform) for better performance.

## Running the example

```plaintext
$ mvn winter:run
2021-04-26 10:15:53,299 INFO  [main] i.w.c.v.Application - Winter is starting...


     ╔════════════════════════════════════════════════════════════════════════════════════════════╗
     ║                       , ~~ ,                                                               ║
     ║                   , '   /\   ' ,                 _                                         ║
     ║                  , __   \/   __ ,       _     _ (_)        _                               ║
     ║                 ,  \_\_\/\/_/_/  ,     | | _ | | _   ___  | |_   ___   __                  ║
     ║                 ,    _\_\/_/_    ,     | |/_\| || | / _ \ | __| / _ \ / _|                 ║
     ║                 ,   __\_/\_\__   ,     \  / \  /| || | | || |_ |  __/| |                   ║
     ║                  , /_/ /\/\ \_\ ,       \/   \/ |_||_| |_| \__| \___||_|                   ║
     ║                   ,     /\     ,                                                           ║
     ║                     ,   \/   ,                        -- 1.0.2-SNAPSHOT --                 ║
     ║                       ' -- '                                                               ║
     ╠════════════════════════════════════════════════════════════════════════════════════════════╣
     ║ Java runtime        : OpenJDK Runtime Environment                                          ║
     ║ Java version        : 11.0.11+9-post-Debian-1deb10u1                                       ║
     ║ Java home           : /usr/lib/jvm/java-11-openjdk-amd64                                   ║
     ║                                                                                            ║
     ║ Application module  : io.winterframework.example.app_http                                  ║
     ║ Application class   : io.winterframework.example.app_http.Main                             ║
     ║                                                                                            ║
     ║ Modules             :                                                                      ║
     ║  * ...                                                                                     ║
     ╚════════════════════════════════════════════════════════════════════════════════════════════╝


2021-04-26 10:15:53,303 INFO  [main] i.w.e.a.App_http - Starting Module io.winterframework.example.app_http...
2021-04-26 10:15:53,304 INFO  [main] i.w.m.b.Boot - Starting Module io.winterframework.mod.boot...
2021-04-26 10:15:53,504 INFO  [main] i.w.m.b.Boot - Module io.winterframework.mod.boot started in 200ms
2021-04-26 10:15:53,504 INFO  [main] i.w.m.h.s.Server - Starting Module io.winterframework.mod.http.server...
2021-04-26 10:15:53,504 INFO  [main] i.w.m.h.b.Base - Starting Module io.winterframework.mod.http.base...
2021-04-26 10:15:53,508 INFO  [main] i.w.m.h.b.Base - Module io.winterframework.mod.http.base started in 4ms
2021-04-26 10:15:53,915 INFO  [main] i.w.m.h.s.i.HttpServer - HTTP Server (epoll) listening on https://0.0.0.0:8443
2021-04-26 10:15:53,916 INFO  [main] i.w.m.h.s.Server - Module io.winterframework.mod.http.server started in 411ms
2021-04-26 10:15:53,916 INFO  [main] i.w.e.a.App_http - Module io.winterframework.example.app_http started in 613ms
```

We can test the application using `curl`:

```plaintext
$ curl --insecure -i https://127.0.0.1:8443
HTTP/2 200 
content-length: 16

Hello from main!
```

## Going further

- [HTTP server module documentation][winter-mod-http-server]
- [Winter core documentation][winter-root-doc]
- [API documentation][javadoc]