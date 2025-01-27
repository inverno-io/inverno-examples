[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-dist-root]: https://github.com/inverno-io/inverno-dist
[inverno-tool-maven-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-maven-plugin
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[inverno-tool-grpc-protoc-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-grpc-protoc-plugin

[inverno-mod-grpc-client]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-grpc-client/
[inverno-mod-http-client]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-http-client/

[inverno-example-grpc-server]: ../inverno-example-grpc-server

[epoll]: https://en.wikipedia.org/wiki/Epoll
[graalvm]: https://www.graalvm.org/
[logback]: https://logback.qos.ch/

# Inverno gRPC client example

A sample Inverno application showing how to invoke gRPC service methods over HTTP/2.

The HTTP client configuration is exposed in the module's configuration `AppConfiguration`. The configuration in `src/main/resources/configuration.cprops` sets the HTTP client to only create plain HTTP/2 connections. The HTTP client is also configured to use [epoll][epoll] when available (i.e. on Linux platform) for better performance.

The application successively invokes the methods from the `Greeter` and `HelloService` services exposed by the [Inverno gRPC server example application][inverno-example-grpc-server] and writes response messages to the standard output. These services are respectively defined in `helloworld/helloworld.proto` and `examples/hello.proto` under `src/main/proto`. The message and client stub classes are generated at build time when the Protocol buffer Maven plugin is invoked with the [Inverno gRPC protoc plugin][inverno-tool-grpc-protoc-plugin].

## Running the application

The application is started using the Inverno Maven plugin as follows:

```plaintext
$ mvn inverno:run
...
2024-12-09 13:59:50,138 INFO  [main] i.i.c.v.Application - Inverno is starting...


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
     ║ Application module  : io.inverno.example.app_grpc_client                                   ║
     ║ Application version : 1.0.0-SNAPSHOT                                                       ║
     ║ Application class   : io.inverno.example.app_grpc_client.Main                              ║
     ║                                                                                            ║
     ║ Modules             :                                                                      ║
     ║  ...                                                                                       ║
     ╚════════════════════════════════════════════════════════════════════════════════════════════╝


2024-12-09 13:59:50,148 INFO  [main] i.i.e.a.App_grpc_client - Starting Module io.inverno.example.app_grpc_client...
2024-12-09 13:59:50,148 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2024-12-09 13:59:50,389 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 240ms
2024-12-09 13:59:50,389 INFO  [main] i.i.m.d.h.Http - Starting Module io.inverno.mod.discovery.http...
2024-12-09 13:59:50,390 INFO  [main] i.i.m.h.c.Client - Starting Module io.inverno.mod.http.client...
2024-12-09 13:59:50,390 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-12-09 13:59:50,404 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 13ms
2024-12-09 13:59:50,527 INFO  [main] i.i.m.h.c.Client - Module io.inverno.mod.http.client started in 137ms
2024-12-09 13:59:50,528 INFO  [main] i.i.m.d.h.Http - Module io.inverno.mod.discovery.http started in 138ms
2024-12-09 13:59:50,528 INFO  [main] i.i.m.g.c.Client - Starting Module io.inverno.mod.grpc.client...
2024-12-09 13:59:50,528 INFO  [main] i.i.m.g.b.Base - Starting Module io.inverno.mod.grpc.base...
2024-12-09 13:59:50,531 INFO  [main] i.i.m.g.b.Base - Module io.inverno.mod.grpc.base started in 2ms
2024-12-09 13:59:50,533 INFO  [main] i.i.m.g.c.Client - Module io.inverno.mod.grpc.client started in 4ms
2024-12-09 13:59:50,533 INFO  [main] i.i.m.w.c.Client - Starting Module io.inverno.mod.web.client...
2024-12-09 13:59:50,533 INFO  [main] i.i.m.w.b.Base - Starting Module io.inverno.mod.web.base...
2024-12-09 13:59:50,533 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-12-09 13:59:50,534 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 0ms
2024-12-09 13:59:50,535 INFO  [main] i.i.m.w.b.Base - Module io.inverno.mod.web.base started in 1ms
2024-12-09 13:59:50,538 INFO  [main] i.i.m.w.c.Client - Module io.inverno.mod.web.client started in 5ms
2024-12-09 13:59:50,550 INFO  [main] i.i.e.a.App_grpc_client - Module io.inverno.example.app_grpc_client started in 408ms
2024-12-09 13:59:50,550 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_grpc_client started in 465ms
2024-12-09 13:59:50,560 INFO  [main] i.i.e.a.Main - Calling /helloworld.Greeter/SayHello
2024-12-09 13:59:50,678 INFO  [inverno-io-epoll-1-2] i.i.m.h.c.i.AbstractEndpoint - HTTP/2.0 Client (epoll) connected to http://127.0.0.1:8080
2024-12-09 13:59:50,722 INFO  [inverno-io-epoll-1-2] i.i.e.a.Main - Received: Hello Bill
2024-12-09 13:59:50,737 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHello
2024-12-09 13:59:50,765 INFO  [inverno-io-epoll-1-5] i.i.m.h.c.i.AbstractEndpoint - HTTP/2.0 Client (epoll) connected to http://127.0.0.1:8080
2024-12-09 13:59:50,792 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Bob
2024-12-09 13:59:50,792 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEverybody
2024-12-09 13:59:50,807 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Bob, Alice, John
2024-12-09 13:59:50,807 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEveryone
2024-12-09 13:59:50,815 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Bob
2024-12-09 13:59:50,815 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Alice
2024-12-09 13:59:50,815 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello John
2024-12-09 13:59:50,816 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEveryoneInTheGroup
2024-12-09 13:59:50,825 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Bob
2024-12-09 13:59:50,826 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Alice
2024-12-09 13:59:50,826 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello John
2024-12-09 13:59:50,826 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEveryoneInTheGroups
2024-12-09 13:59:50,832 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Bob
2024-12-09 13:59:50,832 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Alice
2024-12-09 13:59:50,832 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello John
2024-12-09 13:59:50,832 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Bill
2024-12-09 13:59:50,833 INFO  [main] i.i.e.a.App_grpc_client - Stopping Module io.inverno.example.app_grpc_client...
2024-12-09 13:59:50,840 INFO  [main] i.i.m.b.Boot - Stopping Module io.inverno.mod.boot...
2024-12-09 13:59:50,841 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot stopped in 0ms
2024-12-09 13:59:50,841 INFO  [main] i.i.m.d.h.Http - Stopping Module io.inverno.mod.discovery.http...
2024-12-09 13:59:50,841 INFO  [main] i.i.m.d.h.Http - Module io.inverno.mod.discovery.http stopped in 0ms
2024-12-09 13:59:50,841 INFO  [main] i.i.m.g.c.Client - Stopping Module io.inverno.mod.grpc.client...
2024-12-09 13:59:50,842 INFO  [main] i.i.m.g.b.Base - Stopping Module io.inverno.mod.grpc.base...
2024-12-09 13:59:50,842 INFO  [main] i.i.m.g.b.Base - Module io.inverno.mod.grpc.base stopped in 0ms
2024-12-09 13:59:50,842 INFO  [main] i.i.m.g.c.Client - Module io.inverno.mod.grpc.client stopped in 0ms
2024-12-09 13:59:50,842 INFO  [main] i.i.m.h.c.Client - Stopping Module io.inverno.mod.http.client...
2024-12-09 13:59:50,842 INFO  [main] i.i.m.h.b.Base - Stopping Module io.inverno.mod.http.base...
2024-12-09 13:59:50,842 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base stopped in 0ms
2024-12-09 13:59:50,842 INFO  [main] i.i.m.h.c.Client - Module io.inverno.mod.http.client stopped in 0ms
2024-12-09 13:59:50,842 INFO  [main] i.i.m.w.c.Client - Stopping Module io.inverno.mod.web.client...
2024-12-09 13:59:50,843 INFO  [main] i.i.m.w.b.Base - Stopping Module io.inverno.mod.web.base...
2024-12-09 13:59:50,843 INFO  [main] i.i.m.h.b.Base - Stopping Module io.inverno.mod.http.base...
2024-12-09 13:59:50,843 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base stopped in 0ms
2024-12-09 13:59:50,843 INFO  [main] i.i.m.w.b.Base - Module io.inverno.mod.web.base stopped in 0ms
2024-12-09 13:59:50,843 INFO  [main] i.i.m.w.c.Client - Module io.inverno.mod.web.client stopped in 0ms
2024-12-09 13:59:50,843 INFO  [main] i.i.e.a.App_grpc_client - Module io.inverno.example.app_grpc_client stopped in 10ms

```

It accepts option `--target` which allows to specify the target Inet socket address which defaults to `localhost:8080`:

```plaintext
$ mvn inverno:run --Dinverno.run.arguments="--target=\\\"localhost:9090\\\""
...
2024-04-09 10:17:41,230 INFO  [inverno-io-epoll-1-1] i.i.m.h.c.i.AbstractEndpoint - HTTP/2.0 Client (epoll) connected to http://localhost:9090
...
```

## Packaging the application

The application can be packaged as a native runtime image by invoking the `release` build profile:

```plaintext
$ mvn install -Prelease
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:package-app (inverno-package-app) @ inverno-example-grpc-client ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project application archives created: zip
[INFO] 
[INFO] --- install:3.1.1:install (default-install) @ inverno-example-grpc-client ---
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-grpc-client/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-grpc-client/1.0.0-SNAPSHOT/inverno-example-grpc-client-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-grpc-client/target/inverno-example-grpc-client-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-grpc-client/1.0.0-SNAPSHOT/inverno-example-grpc-client-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-grpc-client/target/inverno-example-grpc-client-1.0.0-SNAPSHOT-application_linux_amd64.zip to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-grpc-client/1.0.0-SNAPSHOT/inverno-example-grpc-client-1.0.0-SNAPSHOT-application_linux_amd64.zip
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The previous command should have created folder `target/inverno-example-grpc-client-1.0.0-SNAPSHOT-application_linux_amd64` containing the Java runtime and the application and installed the corresponding archive to the Maven repository:

```plaintext
$ ./target/inverno-example-grpc-client-1.0.0-SNAPSHOT-application_linux_amd64/bin/example-grpc-client
...
```

A portable docker image of the application can be created as a `tar` archive by invoking the `release-image` build profile:

```plaintext
$ mvn install -Prelease-image
...
[INFO] --- inverno-maven-plugin:${VERSION_INVERNO_TOOLS}:package-image (inverno-package-image) @ inverno-example-grpc-client ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project Docker container image TAR archive created
[INFO] 
[INFO] --- maven-install-plugin:2.5.2:install (default-install) @ inverno-example-grpc-client ---
[INFO] Installing /home/jkuhn/Devel/git/frmk/inverno/inverno-examples/inverno-example-grpc-client/target/inverno-example-grpc-client-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-grpc-client/1.0.0-SNAPSHOT/inverno-example-grpc-client-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/frmk/inverno/inverno-examples/inverno-example-grpc-client/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-grpc-client/1.0.0-SNAPSHOT/inverno-example-grpc-client-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/frmk/inverno/inverno-examples/inverno-example-grpc-client/target/inverno-example-grpc-client-1.0.0-SNAPSHOT-container_linux_amd64.tar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-grpc-client/1.0.0-SNAPSHOT/inverno-example-grpc-client-1.0.0-SNAPSHOT-container_linux_amd64.tar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The previous command should create archive `target/inverno-example-grpc-client-1.0.0-SNAPSHOT-container_linux_amd64.tar` docker image that can be loaded into docker as follows:

```plaintext
$ docker load --input target/inverno-example-grpc-client-1.0.0-SNAPSHOT-container_linux_amd64.tar
```

The application can be directly deployed to a local docker daemon by invoking the `install-image` build profile:

```plaintext
$ mvn install -Pinstall-image
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:install-image (inverno-install-image) @ inverno-example-grpc-client ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project Docker container image deployed to Docker daemon
[INFO] Project image inverno-example-grpc-client:1.0.0-SNAPSHOT installed to Docker
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The application can then be started in docker as follows:

```plaintext
$ docker run --rm --network host inverno-example-grpc-client:1.0.0-SNAPSHOT 
...
2024-04-09 09:38:31,245 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_grpc_client started in 419ms
2024-04-09 09:38:31,259 INFO  [main] i.i.e.a.Main - Calling /helloworld.Greeter/SayHello
2024-04-09 09:38:31,368 INFO  [inverno-io-epoll-1-1] i.i.m.h.c.i.AbstractEndpoint - HTTP/2.0 Client (epoll) connected to http://127.0.0.1:8080
2024-04-09 09:38:31,483 INFO  [inverno-io-epoll-1-1] i.i.e.a.Main - Received: Hello Bill
2024-04-09 09:38:31,492 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHello
2024-04-09 09:38:31,499 INFO  [inverno-io-epoll-1-3] i.i.m.h.c.i.AbstractEndpoint - HTTP/2.0 Client (epoll) connected to http://127.0.0.1:8080
2024-04-09 09:38:31,509 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Bob
2024-04-09 09:38:31,509 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEverybody
2024-04-09 09:38:31,521 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Bob, Alice, John
2024-04-09 09:38:31,522 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEveryone
2024-04-09 09:38:31,528 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Bob
2024-04-09 09:38:31,529 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Alice
2024-04-09 09:38:31,530 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello John
2024-04-09 09:38:31,531 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEveryoneInTheGroup
2024-04-09 09:38:31,540 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Bob
2024-04-09 09:38:31,540 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Alice
2024-04-09 09:38:31,541 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello John
2024-04-09 09:38:31,541 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEveryoneInTheGroups
2024-04-09 09:38:31,549 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Bob
2024-04-09 09:38:31,549 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Alice
2024-04-09 09:38:31,550 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello John
2024-04-09 09:38:31,550 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - Received: Hello Bill
2024-04-09 09:38:31,552 INFO  [main] i.i.e.a.App_grpc_client - Stopping Module io.inverno.example.app_grpc_client...
```

## Building a native image

Using [GraalVM][graalvm], you can also build a native image of the application with the following command:

```plaintext
> mvn clean package -Pnative
```

You can then run the native application:

```plaintext
> ./target/example-grpc-client
2024-12-09 14:08:43,080 INFO  [main] i.i.c.v.Application - Inverno is starting...


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


2024-12-09 14:08:43,080 INFO  [main] i.i.e.a.App_grpc_client - Starting Module io.inverno.example.app_grpc_client...
2024-12-09 14:08:43,080 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2024-12-09 14:08:43,086 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 6ms
2024-12-09 14:08:43,086 INFO  [main] i.i.m.d.h.Http - Starting Module io.inverno.mod.discovery.http...
2024-12-09 14:08:43,086 INFO  [main] i.i.m.h.c.Client - Starting Module io.inverno.mod.http.client...
2024-12-09 14:08:43,086 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-12-09 14:08:43,086 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 0ms
2024-12-09 14:08:43,088 INFO  [main] i.i.m.h.c.Client - Module io.inverno.mod.http.client started in 2ms
2024-12-09 14:08:43,088 INFO  [main] i.i.m.d.h.Http - Module io.inverno.mod.discovery.http started in 2ms
2024-12-09 14:08:43,088 INFO  [main] i.i.m.g.c.Client - Starting Module io.inverno.mod.grpc.client...
2024-12-09 14:08:43,088 INFO  [main] i.i.m.g.b.Base - Starting Module io.inverno.mod.grpc.base...
2024-12-09 14:08:43,088 INFO  [main] i.i.m.g.b.Base - Module io.inverno.mod.grpc.base started in 0ms
2024-12-09 14:08:43,088 INFO  [main] i.i.m.g.c.Client - Module io.inverno.mod.grpc.client started in 0ms
2024-12-09 14:08:43,088 INFO  [main] i.i.m.w.c.Client - Starting Module io.inverno.mod.web.client...
2024-12-09 14:08:43,088 INFO  [main] i.i.m.w.b.Base - Starting Module io.inverno.mod.web.base...
2024-12-09 14:08:43,088 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-12-09 14:08:43,088 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 0ms
2024-12-09 14:08:43,088 INFO  [main] i.i.m.w.b.Base - Module io.inverno.mod.web.base started in 0ms
2024-12-09 14:08:43,088 INFO  [main] i.i.m.w.c.Client - Module io.inverno.mod.web.client started in 0ms
2024-12-09 14:08:43,088 INFO  [main] i.i.e.a.App_grpc_client - Module io.inverno.example.app_grpc_client started in 8ms
2024-12-09 14:08:43,088 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_grpc_client started in 9ms
2024-12-09 14:08:43,089 INFO  [main] i.i.e.a.Main - Calling /helloworld.Greeter/SayHello
2024-12-09 14:08:43,094 INFO  [inverno-io-epoll-1-2] i.i.m.h.c.i.AbstractEndpoint - HTTP/2.0 Client (epoll) connected to http://127.0.0.1:8080
2024-12-09 14:08:43,100 INFO  [inverno-io-epoll-1-2] i.i.e.a.Main - Received: Hello Bill
2024-12-09 14:08:43,100 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHello
2024-12-09 14:08:43,103 INFO  [inverno-io-epoll-1-5] i.i.m.h.c.i.AbstractEndpoint - HTTP/2.0 Client (epoll) connected to http://127.0.0.1:8080
2024-12-09 14:08:43,110 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Bob
2024-12-09 14:08:43,110 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEverybody
2024-12-09 14:08:43,113 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Bob, Alice, John
2024-12-09 14:08:43,114 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEveryone
2024-12-09 14:08:43,116 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Bob
2024-12-09 14:08:43,116 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Alice
2024-12-09 14:08:43,116 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello John
2024-12-09 14:08:43,116 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEveryoneInTheGroup
2024-12-09 14:08:43,119 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Bob
2024-12-09 14:08:43,120 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Alice
2024-12-09 14:08:43,120 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello John
2024-12-09 14:08:43,120 INFO  [main] i.i.e.a.Main - Calling /examples.HelloService/SayHelloToEveryoneInTheGroups
2024-12-09 14:08:43,122 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Bob
2024-12-09 14:08:43,122 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Alice
2024-12-09 14:08:43,123 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello John
2024-12-09 14:08:43,123 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - Received: Hello Bill
2024-12-09 14:08:43,123 INFO  [main] i.i.e.a.App_grpc_client - Stopping Module io.inverno.example.app_grpc_client...
2024-12-09 14:08:43,123 INFO  [main] i.i.m.b.Boot - Stopping Module io.inverno.mod.boot...
2024-12-09 14:08:43,123 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot stopped in 0ms
2024-12-09 14:08:43,123 INFO  [main] i.i.m.d.h.Http - Stopping Module io.inverno.mod.discovery.http...
2024-12-09 14:08:43,123 INFO  [main] i.i.m.d.h.Http - Module io.inverno.mod.discovery.http stopped in 0ms
2024-12-09 14:08:43,123 INFO  [main] i.i.m.g.c.Client - Stopping Module io.inverno.mod.grpc.client...
2024-12-09 14:08:43,123 INFO  [main] i.i.m.g.b.Base - Stopping Module io.inverno.mod.grpc.base...
2024-12-09 14:08:43,123 INFO  [main] i.i.m.g.b.Base - Module io.inverno.mod.grpc.base stopped in 0ms
2024-12-09 14:08:43,123 INFO  [main] i.i.m.g.c.Client - Module io.inverno.mod.grpc.client stopped in 0ms
2024-12-09 14:08:43,123 INFO  [main] i.i.m.h.c.Client - Stopping Module io.inverno.mod.http.client...
2024-12-09 14:08:43,123 INFO  [main] i.i.m.h.b.Base - Stopping Module io.inverno.mod.http.base...
2024-12-09 14:08:43,123 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base stopped in 0ms
2024-12-09 14:08:43,123 INFO  [main] i.i.m.h.c.Client - Module io.inverno.mod.http.client stopped in 0ms
2024-12-09 14:08:43,123 INFO  [main] i.i.m.w.c.Client - Stopping Module io.inverno.mod.web.client...
2024-12-09 14:08:43,123 INFO  [main] i.i.m.w.b.Base - Stopping Module io.inverno.mod.web.base...
2024-12-09 14:08:43,123 INFO  [main] i.i.m.h.b.Base - Stopping Module io.inverno.mod.http.base...
2024-12-09 14:08:43,123 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base stopped in 0ms
2024-12-09 14:08:43,123 INFO  [main] i.i.m.w.b.Base - Module io.inverno.mod.web.base stopped in 0ms
2024-12-09 14:08:43,123 INFO  [main] i.i.m.w.c.Client - Module io.inverno.mod.web.client stopped in 0ms
2024-12-09 14:08:43,123 INFO  [main] i.i.e.a.App_grpc_client - Module io.inverno.example.app_grpc_client stopped in 0ms
```

> Note that for the native image to work, [logback][logback] must be used as logging manager since log4j doesn't support native build (see https://issues.apache.org/jira/browse/LOG4J2-2649).

## Going further

- [gRPC client module documentation][inverno-mod-grpc-client]
- [HTTP client module documentation][inverno-mod-http-client]
- [Inverno gRPC protoc plugin documentation][inverno-tool-grpc-protoc-plugin]
- [Inverno distribution documentation][inverno-dist-root]
- [Inverno Maven plugin documentation][inverno-tool-maven-plugin]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]