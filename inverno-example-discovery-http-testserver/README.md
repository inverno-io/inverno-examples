[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-dist-root]: https://github.com/inverno-io/inverno-dist
[inverno-tool-maven-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-maven-plugin
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[inverno-mod-discovery]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery/
[inverno-mod-discovery-http]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery-http/
[inverno-mod-discovery-http-config]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery-http-config/
[inverno-mod-discovery-http-k8s]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery-http-config-k8s/
[inverno-mod-http-server]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-http-server/

[inverno-examples-discovery-http]: ../inverno-example-discovery-http
[inverno-examples-discovery-http-k8s]: ../inverno-example-discovery-http-k8s

[epoll]: https://en.wikipedia.org/wiki/Epoll
[graalvm]: https://www.graalvm.org/
[logback]: https://logback.qos.ch/
[kubernetes]: https://kubernetes.io
[minikube]: https://minikube.sigs.k8s.io/

# Inverno Discovery HTTP example Test server

A sample test HTTP server application for running the Inverno Discovery HTTP examples.

The Inverno Discovery HTTP provides different `DiscoveryService` implementations: DNS resolution, Configuration resolution with client-based routing and load balancing, Kubernetes service resolution... The test server allows to spawn multiple HTTP servers in order to test these features.

The HTTP server configuration is exposed in the module's configuration `AppConfiguration`.

The server is configured to use [epoll][epoll] when available (i.e. on Linux platform) for better performance.

## Running the application

The application is started using the Inverno Maven plugin as follows:

```plaintext
$ mvn inverno:run
...
2024-09-11 10:19:33,129 INFO  [main] i.i.c.v.Application - Inverno is starting...                    ] Running project...


     ╔════════════════════════════════════════════════════════════════════════════════════════════╗
     ║                      , ~~ ,                                                                ║
     ║                  , '   /\   ' ,                                                            ║
     ║                 , __   \/   __ ,      _                                                    ║
     ║                ,  \_\_\/\/_/_/  ,    | |  ___  _    _  ___   __  ___   ___                 ║
     ║                ,    _\_\/_/_    ,    | | / _ \\ \  / // _ \ / _|/ _ \ / _ \                ║
     ║                ,   __\_/\_\__   ,    | || | | |\ \/ /|  __/| | | | | | |_| |               ║
     ║                 , /_/ /\/\ \_\ ,     |_||_| |_| \__/  \___||_| |_| |_|\___/                ║
     ║                  ,     /\     ,                                                            ║
     ║                    ,   \/   ,                        -- 1.6.0-SNAPSHOT --                  ║
     ║                      ' -- '                                                                ║
     ╠════════════════════════════════════════════════════════════════════════════════════════════╣
     ║ Java runtime        : OpenJDK Runtime Environment                                          ║
     ║ Java version        : 21.0.2+13-58                                                         ║
     ║ Java home           : /home/jkuhn/Devel/jdk/jdk-21.0.2                                     ║
     ║                                                                                            ║
     ║ Application module  : io.inverno.example.app_discovery_http_testserver                     ║
     ║ Application version : 1.0.0-SNAPSHOT                                                       ║
     ║ Application class   : io.inverno.example.app_discovery_http_testserver.Main                ║
     ║                                                                                            ║
     ║ Modules             :                                                                      ║
     ║  ...                                                                                       ║
     ╚════════════════════════════════════════════════════════════════════════════════════════════╝


2024-09-11 10:19:33,139 INFO  [main] i.i.e.a.App_discovery_http_testserver - Starting Module io.inverno.example.app_discovery_http_testserver...
2024-09-11 10:19:33,139 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2024-09-11 10:19:33,378 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 238ms
2024-09-11 10:19:33,378 INFO  [main] i.i.m.h.s.Server - Starting Module io.inverno.mod.http.server...
2024-09-11 10:19:33,378 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-09-11 10:19:33,383 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 5ms
2024-09-11 10:19:33,532 INFO  [main] i.i.m.h.s.i.HttpServer - HTTP Server (epoll) listening on http://0.0.0.0:8080
2024-09-11 10:19:33,532 INFO  [main] i.i.m.h.s.Server - Module io.inverno.mod.http.server started in 154ms
2024-09-11 10:19:33,533 INFO  [main] i.i.e.a.App_discovery_http_testserver - Module io.inverno.example.app_discovery_http_testserver started in 400ms
2024-09-11 10:19:33,533 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_discovery_http_testserver started in 446ms
```

In order to test service discovery, multiple servers listening on different ports must be started, the HTTP server port can be specified on the command line as follows:

```plaintext
$ mvn inverno:run -Dinverno.run.arguments="--io.inverno.example.app_discovery_http_testserver.appConfiguration.http_server.server_port=8081"
...
2024-09-11 10:22:35,986 INFO  [main] i.i.m.h.s.i.HTTPServer - HTTP Server (epoll) listening on http://0.0.0.0:8081
...
```

The server responds to any request with a JSON object specifying the local and remote addresses, the request method, path, authority and headers:

```plaintext
$ curl -i http://127.0.0.1:8081/some_path?k=v
HTTP/1.1 200 OK
content-type: application/json
content-length: 195

{"localAddress":"/127.0.0.1:8080", "remoteAddress":"/127.0.0.1:58604", "method":"GET", "path":"/some_path?k=v", "authority":"127.0.0.1:8080", "headers":{"Host":"127.0.0.1:8080", "User-Agent":"curl/7.88.1", "Accept":"*/*"}}
```

Such response allows to easily determine to which server instance a request has been routed or load balanced and whether path, authority or headers were rewritten. 

## Packaging the application

The application can be packaged as a native runtime image by invoking the `release` build profile:

```plaintext
$ mvn install -Prelease
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:package-app (inverno-package-app) @ inverno-example-discovery-http-testserver ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] 
[INFO] 
[INFO] --- install:3.1.1:install (default-install) @ inverno-example-discovery-http-testserver ---
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http-testserver/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http-testserver/1.0.0-SNAPSHOT/inverno-example-discovery-http-testserver-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http-testserver/target/inverno-example-discovery-http-testserver-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http-testserver/1.0.0-SNAPSHOT/inverno-example-discovery-http-testserver-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http-testserver/target/inverno-example-discovery-http-testserver-1.0.0-SNAPSHOT-application_linux_amd64.zip to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http-testserver/1.0.0-SNAPSHOT/inverno-example-discovery-http-testserver-1.0.0-SNAPSHOT-application_linux_amd64.zip
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The previous command creates folder `target/inverno-example-discovery-http-testserver-1.0.0-SNAPSHOT-application_linux_amd64` containing the Java runtime and the application and installed the corresponding archive to the Maven repository:

The application can now be started with the following native command:

```plaintext
$ ./target/inverno-example-discovery-http-testserver-1.0.0-SNAPSHOT-application_linux_amd64/bin/example-discovery-http-testserver --io.inverno.example.app_discovery_http_testserver.appConfiguration.http_server.server_port=8081
...
```

A portable docker image of the application can be created as a `tar` archive by invoking the `release-image` build profile:

```plaintext
$ mvn install -Prelease-image
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:package-image (inverno-package-image) @ inverno-example-discovery-http-testserver ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project Docker container image TAR archive created
[INFO] 
[INFO] --- install:3.1.1:install (default-install) @ inverno-example-discovery-http-testserver ---
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http-testserver/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http-testserver/1.0.0-SNAPSHOT/inverno-example-discovery-http-testserver-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http-testserver/target/inverno-example-discovery-http-testserver-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http-testserver/1.0.0-SNAPSHOT/inverno-example-discovery-http-testserver-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http-testserver/target/inverno-example-discovery-http-testserver-1.0.0-SNAPSHOT-container_linux_amd64.tar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http-testserver/1.0.0-SNAPSHOT/inverno-example-discovery-http-testserver-1.0.0-SNAPSHOT-container_linux_amd64.tar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The previous command creates archive `target/inverno-example-discovery-http-testserver-1.0.0-SNAPSHOT-container_linux_amd64.tar` docker image that can be loaded into docker as follows:

```plaintext
$ `docker load --input target/inverno-example-discovery-http-testserver-1.0.0-SNAPSHOT-container_linux_amd64.tar`
```

The application can be directly deployed to a local docker daemon by invoking the `install-image` build profile:

```plaintext
$ mvn install -Pinstall-image
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:install-image (inverno-install-image) @ inverno-example-discovery-http-testserver ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project Docker container image deployed to Docker daemon
[INFO] Project image `inverno-example-discovery-http-testserver`:1.0.0-SNAPSHOT installed to Docker
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The application can then be started in docker as follows:

```plaintext
$ docker run --rm --network host inverno-example-discovery-http-testserver:1.0.0-SNAPSHOT 
...
2024-09-11 08:37:45,450 INFO  [main] i.i.m.h.s.i.HttpServer - HTTP Server (epoll) listening on http://0.0.0.0:8080
2024-09-11 08:37:45,451 INFO  [main] i.i.m.h.s.Server - Module io.inverno.mod.http.server started in 135ms
2024-09-11 08:37:45,451 INFO  [main] i.i.e.a.App_discovery_http_testserver - Module io.inverno.example.app_discovery_http_testserver started in 361ms
2024-09-11 08:37:45,451 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_discovery_http_testserver started in 396ms
```

## Deploying and running the application on a Kubernetes cluster

The test server can be deployed on a [Kubernetes][kubernetes] cluster, such setup is especially used to run the [Inverno Discovery HTTP Kubernetes example][inverno-examples-discovery-http-k8s].

For the sake of simplicity, we will use a local [minikube][minikube] cluster but the following commands should work on any Kubernetes cluster. Please refer to the minikube documentation to get started, from a proper installation a local cluster can be started as follows:

```plaintext
$ minikube start
...
$ eval $(minikube docker-env)
...
```

The Kubernetes Dashboard can also be started as follows:

```plaintext
$ minikube dashboard
...
```

From there, the console should point to the Kubernetes Docker daemon and the test server application container image can then be deployed as before: 

```plaintext
$ mvn install -Pinstall-image
...
$ docker images
REPOSITORY                                               TAG              IMAGE ID       CREATED          SIZE
inverno-example-discovery-http-testserver                1.0.0-SNAPSHOT   8fe471ed6ff3   14 seconds ago   187MB
...
```

The application can be deployed as a service `example-discovery-http-testserver` with 3 replicas as follows:

```plaintext
$ kubectl apply -f kube/inverno-example-discovery-http-testserver.yaml
service/example-discovery-http-testserver created
deployment.apps/example-discovery-http-testserver created
```

It can also be deployed as a headless service with 3 replicas as follows:

```plaintext
$ kubectl apply -f kube/inverno-example-discovery-http-testserver_headless.yaml 
service/example-discovery-http-testserver-headless created
deployment.apps/example-discovery-http-testserver-headless created
```

> Unlike regular ClusterIP services, no load balanced IP address is assigned to a headless service, the addresses of all the service replicas can then be obtained by querying the DNS server which is particularly useful to implement client side load balancing strategies which is what is demonstrated in [Inverno Discovery HTTP Kubernetes example][inverno-examples-discovery-http-k8s].

Here is how the cluster should look like:

```plaintext
$ kubectl get services
NAME                                         TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
example-discovery-http-testserver            ClusterIP   10.100.186.74   <none>        8080/TCP   16m
example-discovery-http-testserver-headless   ClusterIP   None            <none>        8080/TCP   6s
kubernetes                                   ClusterIP   10.96.0.1       <none>        443/TCP    92d
$ kubectl get deployments
NAME                                         READY   UP-TO-DATE   AVAILABLE   AGE
example-discovery-http-testserver            3/3     3            3           16m
example-discovery-http-testserver-headless   3/3     3            3           11s
$ kubectl get pods
NAME                                                          READY   STATUS    RESTARTS   AGE
example-discovery-http-testserver-789c889788-cggsw            1/1     Running   0          16m
example-discovery-http-testserver-789c889788-smlrr            1/1     Running   0          16m
example-discovery-http-testserver-789c889788-z5h24            1/1     Running   0          16m
example-discovery-http-testserver-headless-646f647586-knk8c   1/1     Running   0          14s
example-discovery-http-testserver-headless-646f647586-n9562   1/1     Running   0          14s
example-discovery-http-testserver-headless-646f647586-s6x4h   1/1     Running   0          14s
```

## Building a native image

Using [GraalVM][graalvm], you can also build a native image of the application with the following command:

```plaintext
$ mvn clean package -Pnative
```

You can then run the native application:

```plaintext
$ ./target/example-discovery-http-testserver
2024-09-11 10:47:01,432 INFO  [main] i.i.c.v.Application - Inverno is starting...


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


2024-09-11 10:47:01,432 INFO  [main] i.i.e.a.App_discovery_http_testserver - Starting Module io.inverno.example.app_discovery_http_testserver...
2024-09-11 10:47:01,432 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2024-09-11 10:47:01,440 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 8ms
2024-09-11 10:47:01,440 INFO  [main] i.i.m.h.s.Server - Starting Module io.inverno.mod.http.server...
2024-09-11 10:47:01,440 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-09-11 10:47:01,440 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 0ms
2024-09-11 10:47:01,444 INFO  [main] i.i.m.h.s.i.HttpServer - HTTP Server (epoll) listening on http://0.0.0.0:8080
2024-09-11 10:47:01,444 INFO  [main] i.i.m.h.s.Server - Module io.inverno.mod.http.server started in 4ms
2024-09-11 10:47:01,444 INFO  [main] i.i.e.a.App_discovery_http_testserver - Module io.inverno.example.app_discovery_http_testserver started in 12ms
2024-09-11 10:47:01,444 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_discovery_http_testserver started in 13ms
```

> Note that for the native image to work, [logback][logback] must be used as logging manager since log4j doesn't support native build (see https://issues.apache.org/jira/browse/LOG4J2-2649).

## Going further

- [HTTP server module documentation][inverno-mod-http-server]
- [Discovery API module documentation][inverno-mod-discovery]
- [Discovery HTTP module documentation][inverno-mod-discovery-http]
- [Discovery HTTP Configuration module documentation][inverno-mod-discovery-http-config]
- [Discovery HTTP Kubernetes module documentation][inverno-mod-discovery-http-k8s]
- [Inverno distribution documentation][inverno-dist-root]
- [Inverno Maven plugin documentation][inverno-tool-maven-plugin]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]