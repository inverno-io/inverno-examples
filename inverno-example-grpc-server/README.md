[inverno-example-grpc-client]: ../inverno-example-grpc-client

[inverno-mod-grpc-server]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-grpc-server/
[inverno-mod-http-server]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-http-server/
[inverno-mod-web-server]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-web-server/
[inverno-dist-root]: https://github.com/inverno-io/inverno-dist
[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-tool-maven-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-maven-plugin
[inverno-tool-grpc-protoc-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-grpc-protoc-plugin
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[epoll]: https://en.wikipedia.org/wiki/Epoll
[grpc-helloworld-example]: https://github.com/grpc/grpc-java/blob/master/examples/src/main/proto/helloworld.proto
[grpc-java-helloworld-example]: https://github.com/grpc/grpc-java/tree/master/examples/src/main/java/io/grpc/examples/helloworld

[graalvm]: https://www.graalvm.org/
[logback]: https://logback.qos.ch/

# Inverno gRPC server example

A sample Inverno application showing how to expose gRPC service methods in the [Inverno Web server][inverno-mod-web-server];

The Web server configuration is exposed in the module's configuration `App_grpc_serverConfiguration`. The configuration in `src/main/resources/configuration.cprops` sets the HTTP server to accept plain HTTP/2 connections. The HTTP server is also configured to use [epoll][epoll] when available (ie. on Linux platform) for better performance.

The application implements and exposes the `Greeter` and `HelloService` services which are respectively defined in `helloworld/helloworld.proto` and `examples/hello.proto` under `src/main/proto`. The message and routes configurer stub classes are generated at build time when the Protocol buffer Maven plugin is invoked with the [Inverno gRPC protoc plugin][inverno-tool-grpc-protoc-plugin].

## Running the application

The application is started using the Inverno Maven plugin as follows:

```plaintext
$ mvn inverno:run
...
2024-04-09 11:57:05,009 INFO  [main] i.i.c.v.Application - Inverno is starting...                    ] Running project...


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
     ║ Application module  : io.inverno.example.app_grpc_server                                   ║
     ║ Application version : 1.0.0-SNAPSHOT                                                       ║
     ║ Application class   : io.inverno.example.app_grpc_server.Main                              ║
     ║                                                                                            ║
     ║ Modules             :                                                                      ║
     ║  ...                                                                                       ║
     ╚════════════════════════════════════════════════════════════════════════════════════════════╝


2024-04-09 11:57:05,022 INFO  [main] i.i.e.a.App_grpc_server - Starting Module io.inverno.example.app_grpc_server...
2024-04-09 11:57:05,023 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2024-04-09 11:57:05,245 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 222ms
2024-04-09 11:57:05,246 INFO  [main] i.i.m.g.s.Server - Starting Module io.inverno.mod.grpc.server...
2024-04-09 11:57:05,246 INFO  [main] i.i.m.g.b.Base - Starting Module io.inverno.mod.grpc.base...
2024-04-09 11:57:05,370 INFO  [main] i.i.m.g.b.Base - Module io.inverno.mod.grpc.base started in 124ms
2024-04-09 11:57:05,373 INFO  [main] i.i.m.g.s.Server - Module io.inverno.mod.grpc.server started in 126ms
2024-04-09 11:57:05,373 INFO  [main] i.i.m.w.s.Server - Starting Module io.inverno.mod.web.server...
2024-04-09 11:57:05,373 INFO  [main] i.i.m.h.s.Server - Starting Module io.inverno.mod.http.server...
2024-04-09 11:57:05,373 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-04-09 11:57:05,378 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 4ms
2024-04-09 11:57:05,499 INFO  [main] i.i.m.h.s.i.HttpServer - HTTP Server (epoll) listening on http://0.0.0.0:8080
2024-04-09 11:57:05,500 INFO  [main] i.i.m.h.s.Server - Module io.inverno.mod.http.server started in 127ms
2024-04-09 11:57:05,500 INFO  [main] i.i.m.w.s.Server - Module io.inverno.mod.web.server started in 127ms
2024-04-09 11:57:05,500 INFO  [main] i.i.e.a.App_grpc_server - Module io.inverno.example.app_grpc_server started in 487ms
2024-04-09 11:57:05,501 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_grpc_server started in 558ms
```

It exposes the `Greeter` service defined in [gRPC helloworld example][grpc-helloworld-example] which can be invoked with the [gRPC-java HelloWorld example][grpc-java-helloworld-example]:

```plaintext
$ $GRPC_JAVA_HOME/examples/build/install/examples/bin/hello-world-client Bob localhost:8080
avr. 08, 2024 11:58:42 AM io.grpc.examples.helloworld.HelloWorldClient greet
INFOS: Will try to greet Bob ...
avr. 08, 2024 11:58:42 AM io.grpc.examples.helloworld.HelloWorldClient greet
INFOS: Greeting: Hello Bob
$
```

The [Inverno gRPC client example][inverno-example-grpc-client] application invokes all exposed service methods successively:

```plaintext
$ mvn dependency:unpack -Dartifact=io.inverno.example:inverno-example-grpc-client:1.0.0-SNAPSHOT:zip:application_linux_amd64 -DoutputDirectory=./

$ ./inverno-example-grpc-client-1.0.0-SNAPSHOT/bin/example-grpc-client
...
2024-04-09 10:17:41,112 INFO  [main] i.i.e.a.Main - Calling /helloworld.Greeter/SayHello
2024-04-09 10:17:41,230 INFO  [inverno-io-epoll-1-1] i.i.m.h.c.i.AbstractEndpoint - HTTP/2.0 Client (epoll) connected to http://localhost:8080
2024-04-09 10:17:41,282 INFO  [inverno-io-epoll-1-1] i.i.e.a.Main - Received: Hello Bill
2024-04-09 10:17:41,294 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHello
2024-04-09 10:17:41,303 INFO  [inverno-io-epoll-1-3] i.i.m.h.c.i.AbstractEndpoint - HTTP/2.0 Client (epoll) connected to http://localhost:8080
2024-04-09 10:17:41,307 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Bob
2024-04-09 10:17:41,308 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEverybody
2024-04-09 10:17:41,319 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Bob, Alice, John
2024-04-09 10:17:41,319 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEveryone
2024-04-09 10:17:41,324 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Bob
2024-04-09 10:17:41,325 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Alice
2024-04-09 10:17:41,325 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello John
2024-04-09 10:17:41,326 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEveryoneInTheGroup
2024-04-09 10:17:41,333 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Bob
2024-04-09 10:17:41,333 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Alice
2024-04-09 10:17:41,334 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello John
2024-04-09 10:17:41,334 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEveryoneInTheGroups
2024-04-09 10:17:41,338 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Bob
2024-04-09 10:17:41,339 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Alice
2024-04-09 10:17:41,339 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello John
2024-04-09 10:17:41,339 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Bill
...
```

## Packaging the application

The application can be packaged as a native runtime image by invoking the `release` build profile:

```plaintext
$ mvn install -Prelease
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:package-app (inverno-package-app) @ inverno-example-grpc-server ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project application archives created: zip
[INFO] 
[INFO] --- install:3.1.1:install (default-install) @ inverno-example-grpc-server ---
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-grpc-server/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-grpc-server/1.0.0-SNAPSHOT/inverno-example-grpc-server-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-grpc-server/target/inverno-example-grpc-server-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-grpc-server/1.0.0-SNAPSHOT/inverno-example-grpc-server-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-grpc-server/target/inverno-example-grpc-server-1.0.0-SNAPSHOT-application_linux_amd64.zip to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-grpc-server/1.0.0-SNAPSHOT/inverno-example-grpc-server-1.0.0-SNAPSHOT-application_linux_amd64.zip
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------

```

The previous command should have created folder `target/inverno-example-grpc-server-1.0.0-SNAPSHOT-application_linux_amd64` containing the Java runtime and the application and installed the corresponding archive to the Maven repository:

```plaintext
$ ./target/inverno-example-grpc-server-1.0.0-SNAPSHOT-application_linux_amd64/bin/example-grpc-server
...
```

A portable docker image of the application can be created as a `tar` archive by invoking the `release-image` build profile:

```plaintext
$ mvn install -Prelease-image
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:package-image (inverno-package-image) @ inverno-example-grpc-server ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project Docker container image TAR archive created
[INFO] 
[INFO] --- install:3.1.1:install (default-install) @ inverno-example-grpc-server ---
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-grpc-server/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-grpc-server/1.0.0-SNAPSHOT/inverno-example-grpc-server-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-grpc-server/target/inverno-example-grpc-server-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-grpc-server/1.0.0-SNAPSHOT/inverno-example-grpc-server-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-grpc-server/target/inverno-example-grpc-server-1.0.0-SNAPSHOT-container_linux_amd64.tar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-grpc-server/1.0.0-SNAPSHOT/inverno-example-grpc-server-1.0.0-SNAPSHOT-container_linux_amd64.tar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------

```

The previous command should create archive `target/inverno-example-grpc-server-1.0.0-SNAPSHOT-container_linux_amd64.tar` docker image that can be loaded into docker as follows:

```plaintext
$ docker load --input target/inverno-example-grpc-server-1.0.0-SNAPSHOT-container_linux_amd64.tar
```

The application can be directly deployed to a local docker daemon by invoking the `install-image` build profile:

```plaintext
$ mvn install -Pinstall-image
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:install-image (inverno-install-image) @ inverno-example-grpc-server ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project Docker container image deployed to Docker daemon
[INFO] Project image inverno-example-grpc-server:1.0.0-SNAPSHOT installed to Docker
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The application can then be started in docker as follows:

```plaintext
$ docker run --rm --network host inverno-example-grpc-server:1.0.0-SNAPSHOT 
...
2024-04-09 10:14:19,388 INFO  [main] i.i.e.a.App_grpc_server - Starting Module io.inverno.example.app_grpc_server...
2024-04-09 10:14:19,388 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2024-04-09 10:14:19,584 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 195ms
2024-04-09 10:14:19,584 INFO  [main] i.i.m.g.s.Server - Starting Module io.inverno.mod.grpc.server...
2024-04-09 10:14:19,585 INFO  [main] i.i.m.g.b.Base - Starting Module io.inverno.mod.grpc.base...
2024-04-09 10:14:19,682 INFO  [main] i.i.m.g.b.Base - Module io.inverno.mod.grpc.base started in 97ms
2024-04-09 10:14:19,683 INFO  [main] i.i.m.g.s.Server - Module io.inverno.mod.grpc.server started in 98ms
2024-04-09 10:14:19,683 INFO  [main] i.i.m.w.s.Server - Starting Module io.inverno.mod.web.server...
2024-04-09 10:14:19,684 INFO  [main] i.i.m.h.s.Server - Starting Module io.inverno.mod.http.server...
2024-04-09 10:14:19,684 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-04-09 10:14:19,688 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 3ms
2024-04-09 10:14:19,796 INFO  [main] i.i.m.h.s.i.HttpServer - HTTP Server (epoll) listening on http://0.0.0.0:8080
2024-04-09 10:14:19,796 INFO  [main] i.i.m.h.s.Server - Module io.inverno.mod.http.server started in 112ms
2024-04-09 10:14:19,797 INFO  [main] i.i.m.w.s.Server - Module io.inverno.mod.web.server started in 113ms
2024-04-09 10:14:19,797 INFO  [main] i.i.e.a.App_grpc_server - Module io.inverno.example.app_grpc_server started in 412ms
2024-04-09 10:14:19,797 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_grpc_server started in 457ms
```

## Building a native image

Using [GraalVM][graalvm], you can also build a native image of the application with the following command:

```plaintext
> mvn clean package -Pnative
```

You can then run the native application:

```plaintext
> ./target/inverno-example-grpc-server
2024-04-09 13:01:54,616 INFO  [main] i.i.c.v.Application - Inverno is starting...


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


2024-04-09 13:01:54,616 INFO  [main] i.i.e.a.App_grpc_server - Starting Module io.inverno.example.app_grpc_server...
2024-04-09 13:01:54,616 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2024-04-09 13:01:54,622 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 5ms
2024-04-09 13:01:54,622 INFO  [main] i.i.m.g.s.Server - Starting Module io.inverno.mod.grpc.server...
2024-04-09 13:01:54,622 INFO  [main] i.i.m.g.b.Base - Starting Module io.inverno.mod.grpc.base...
2024-04-09 13:01:54,624 INFO  [main] i.i.m.g.b.Base - Module io.inverno.mod.grpc.base started in 1ms
2024-04-09 13:01:54,624 INFO  [main] i.i.m.g.s.Server - Module io.inverno.mod.grpc.server started in 1ms
2024-04-09 13:01:54,624 INFO  [main] i.i.m.w.s.Server - Starting Module io.inverno.mod.web.server...
2024-04-09 13:01:54,624 INFO  [main] i.i.m.h.s.Server - Starting Module io.inverno.mod.http.server...
2024-04-09 13:01:54,624 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-04-09 13:01:54,624 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 0ms
2024-04-09 13:01:54,625 INFO  [main] i.i.m.h.s.i.HttpServer - HTTP Server (epoll) listening on http://0.0.0.0:8080
2024-04-09 13:01:54,625 INFO  [main] i.i.m.h.s.Server - Module io.inverno.mod.http.server started in 1ms
2024-04-09 13:01:54,626 INFO  [main] i.i.m.w.s.Server - Module io.inverno.mod.web.server started in 1ms
2024-04-09 13:01:54,626 INFO  [main] i.i.e.a.App_grpc_server - Module io.inverno.example.app_grpc_server started in 9ms
2024-04-09 13:01:54,626 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_grpc_server started in 9ms
```

> Note that for the native image to work, [logback][logback] must be used as logging manager since log4j doesn't support native build (see https://issues.apache.org/jira/browse/LOG4J2-2649).

## Going further

- [gRPC server module documentation][inverno-mod-grpc-server]
- [HTTP server module documentation][inverno-mod-http-server]
- [Web server module documentation][inverno-mod-web-server]
- [Inverno distribution documentation][inverno-dist-root]
- [Inverno Maven plugin documentation][inverno-tool-maven-plugin]
- [Inverno gRPC protoc plugin documentation][inverno-tool-grpc-protoc-plugin]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]
