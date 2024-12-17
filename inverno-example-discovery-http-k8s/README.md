[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-dist-root]: https://github.com/inverno-io/inverno-dist
[inverno-tool-maven-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-maven-plugin
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[inverno-mod-discovery]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery/
[inverno-mod-discovery-http]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery-http/
[inverno-mod-discovery-http-metadata]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery-http-metadata/
[inverno-mod-discovery-http-k8s]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery-http-k8s/
[inverno-mod-web-client]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-web-client/
[inverno-mod-web-server]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-web-server/

[inverno-examples-discovery-http]: ../inverno-example-discovery-http
[inverno-examples-discovery-http-testserver]: ../inverno-example-discovery-http-testserver

[epoll]: https://en.wikipedia.org/wiki/Epoll
[graalvm]: https://www.graalvm.org/
[logback]: https://logback.qos.ch/
[kubernetes]: https://kubernetes.io
[minikube]: https://minikube.sigs.k8s.io/

# Inverno Discovery HTTP Kubernetes example

A sample application demonstrating how HTTP services can be resolved in a Kubernetes cluster.

The application is a simple HTTP server exposing a single endpoint acting as a gateway to [test server][inverno-examples-discovery-http-testserver] instances that must be deployed as services in a Kubernetes cluster.

It demonstrates the DNS service resolution in a Kubernetes cluster as well as simple Kubernetes resolution based on environment variables.

The Web client configuration is exposed in the module's configuration `AppConfiguration`.

The server is configured to use [epoll][epoll] when available (i.e. on Linux platform) for better performance.

> The [Discovery HTTP Kubernetes module][inverno-mod-discovery-http-k8s] is currently pretty basic, it only resolves service IP from environment variables. In the future, it will connect to the API server directly to get much more information and support more advanced routing and load balancing features. For the time being, you might want to look at the [Discovery HTTP metadata module][inverno-mod-discovery-http-metadata] which already offers rich and advanced routing and load balancing capabilities.

## Packaging the application

The application can be packaged as a native runtime image by invoking the `release` build profile:

```plaintext
$ mvn install -Prelease
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:package-app (inverno-package-app) @ inverno-example-discovery-http-k8s ---
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http-k8s/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http-k8s/1.0.0-SNAPSHOT/inverno-example-discovery-http-k8s-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http-k8s/target/inverno-example-discovery-http-k8s-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http-k8s/1.0.0-SNAPSHOT/inverno-example-discovery-http-k8s-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http-k8s/target/inverno-example-discovery-http-k8s-1.0.0-SNAPSHOT-application_linux_amd64.zip to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http-k8s/1.0.0-SNAPSHOT/inverno-example-discovery-http-k8s-1.0.0-SNAPSHOT-application_linux_amd64.zip
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The previous command creates folder `target/inverno-example-discovery-http-k8s-1.0.0-SNAPSHOT-application_linux_amd64` containing the Java runtime and the application and installed the corresponding archive to the Maven repository:

The application can now be started with the following native command:

```plaintext
$ ./target/inverno-example-discovery-http-k8s-1.0.0-SNAPSHOT-application_linux_amd64/bin/example-discovery-http-k8s
...
```

A portable docker image of the application can be created as a `tar` archive by invoking the `release-image` build profile:

```plaintext
$ mvn install -Prelease-image
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:package-image (inverno-package-image) @ inverno-example-discovery-http-k8s ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project Docker container image TAR archive created
[INFO] 
[INFO] --- install:3.1.1:install (default-install) @ inverno-example-discovery-http-k8s ---
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http-k8s/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http-k8s/1.0.0-SNAPSHOT/inverno-example-discovery-http-k8s-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http-k8s/target/inverno-example-discovery-http-k8s-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http-k8s/1.0.0-SNAPSHOT/inverno-example-discovery-http-k8s-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http-k8s/target/inverno-example-discovery-http-k8s-1.0.0-SNAPSHOT-container_linux_amd64.tar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http-k8s/1.0.0-SNAPSHOT/inverno-example-discovery-http-k8s-1.0.0-SNAPSHOT-container_linux_amd64.tar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The previous command creates archive `target/inverno-example-discovery-http-k8s-1.0.0-SNAPSHOT-container_linux_amd64.tar` docker image that can be loaded into docker as follows:

```plaintext
$ `docker load --input target/inverno-example-discovery-http-k8s-1.0.0-SNAPSHOT-container_linux_amd64.tar`
```

The application can be directly deployed to a local docker daemon by invoking the `install-image` build profile:

```plaintext
$ mvn install -Pinstall-image
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:install-image (inverno-install-image) @ inverno-example-discovery-http-k8s ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project Docker container image deployed to Docker daemon
[INFO] Project image inverno-example-discovery-http-k8s:1.0.0-SNAPSHOT installed to Docker
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The application can then be started in docker as follows:

```plaintext
$ docker run --rm --network host inverno-example-discovery-http-k8s:1.0.0-SNAPSHOT 
...
2024-09-13 13:39:27,030 INFO  [main] i.i.m.h.s.i.HttpServer - HTTP Server (epoll) listening on http://0.0.0.0:8080
2024-09-13 13:39:27,031 INFO  [main] i.i.m.h.s.Server - Module io.inverno.mod.http.server started in 59ms
2024-09-13 13:39:27,032 INFO  [main] i.i.m.w.s.Server - Module io.inverno.mod.web.server started in 60ms
2024-09-13 13:39:27,079 INFO  [main] i.i.e.a.App_discovery_http_k8s - Module io.inverno.example.app_discovery_http_k8s started in 508ms
2024-09-13 13:39:27,080 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_discovery_http_k8s started in 566ms
```
## Deploying and running the application in a Kubernetes cluster

The application must be deployed on a [Kubernetes][kubernetes] cluster to demonstrate how services are resolved in a Kubernetes, please refer to the [Inverno Discovery HTTP example test server][inverno-examples-discovery-http-testserver] to learn how to start the services targeted by the application.

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
inverno-example-discovery-http-k8s                       1.0.0-SNAPSHOT   48532d2fa1ff   10 seconds ago   190MB
...
```

The application can then be deployed as a service `example-discovery-http-k8s` with 1 replica as follows:

```plaintext
$ kubectl apply -f kube/inverno-example-discovery-http-k8s.yaml
service/example-discovery-http-testserver created
deployment.apps/example-discovery-http-testserver created
```

Assuming the test server services have been started beforehand, here is how the cluster should look like:

```plaintext
$ kubectl get services
NAME                                         TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
example-discovery-http-k8s                   ClusterIP   10.104.39.214   <none>        8080/TCP   59s
example-discovery-http-testserver            ClusterIP   10.100.186.74   <none>        8080/TCP   31m
example-discovery-http-testserver-headless   ClusterIP   None            <none>        8080/TCP   15m
kubernetes                                   ClusterIP   10.96.0.1       <none>        443/TCP    92d
$ kubectl get deployments
NAME                                         READY   UP-TO-DATE   AVAILABLE   AGE
example-discovery-http-k8s                   1/1     1            1           64s
example-discovery-http-testserver            3/3     3            3           31m
example-discovery-http-testserver-headless   3/3     3            3           15m
$ kubectl get pods
NAME                                                          READY   STATUS    RESTARTS   AGE
example-discovery-http-k8s-5c5646f7f5-lb5g7                   1/1     Running   0          71s
example-discovery-http-testserver-789c889788-cggsw            1/1     Running   0          31m
example-discovery-http-testserver-789c889788-smlrr            1/1     Running   0          31m
example-discovery-http-testserver-789c889788-z5h24            1/1     Running   0          31m
example-discovery-http-testserver-headless-646f647586-knk8c   1/1     Running   0          15m
example-discovery-http-testserver-headless-646f647586-n9562   1/1     Running   0          15m
example-discovery-http-testserver-headless-646f647586-s6x4h   1/1     Running   0          15m
```

The application now needs to be exposed locally:

```plaintext
$ minikube service example-discovery-http-k8s
```

The application acts as a gateway to test services `example-discovery-http-testserver` and `example-discovery-http-testserver-headless`, it defines a single route that uses a Web client to resolve the requested service endpoints and propagate the request. It matches `/{scheme}/{path:**}` path where the `{scheme}` path variable specifies the scheme of the service URI to resolve: `http` for a DNS resolution, `k8s-env` to resolve the service clusterIP from the environment variable defined in the container, and `{path:**}` which is the path of the request to send to the resolved test service. The name of the targeted test service must be specified in the `host` (or `:authority`) header.

For instance, the `example-discovery-http-testserver` can be resolved using the DNS discovery service as follows:

```plaintext
$ curl -H 'host: example-discovery-http-testserver:8080' http://127.0.0.1:37807/http/path/to/resource
{"localAddress":"/10.244.0.90:8080","remoteAddress":"/10.244.0.102:38406","method":"GET","path":"/path/to/resource","authority":"example-discovery-http-testserver.default.svc.cluster.local:8080","headers":{"content-length":"0","upgrade":"h2c",":method":"GET",":scheme":"http","connection":"upgrade,http2-settings",":path":"/path/to/resource",":authority":"example-discovery-http-testserver.default.svc.cluster.local:8080","user-agent":"Inverno/1.12.0-SNAPSHOT"}}
```

If we send the request multiple times, we should be able to see that the three replicas are responding one after the other thanks to iptables rules defined by Kubernetes for a ClusterIP service:

```plaintext
$ curl -H 'host: example-discovery-http-testserver:8080' http://127.0.0.1:37807/http/path/to/resource
{"localAddress":"/10.244.0.90:8080",...
$ curl -H 'host: example-discovery-http-testserver:8080' http://127.0.0.1:37807/http/path/to/resource
{"localAddress":"/10.244.0.92:8080",...
$ curl -H 'host: example-discovery-http-testserver:8080' http://127.0.0.1:37807/http/path/to/resource
{"localAddress":"/10.244.0.91:8080",...
```

The Web client is configured with a max pool size of 1 which means only one connection is created which is kept alive for 60 seconds by default, so you might need to wait 60 seconds between two requests to see that replicas are indeed load balanced.

The `example-discovery-http-testserver` can also be resolved from the environment variables sets in the container by Kubernetes for a ClusterIP service:

```plaintext
curl -H 'host: example-discovery-http-testserver' http://127.0.0.1:37807/k8s-env/path/to/resource
{"localAddress":"/10.244.0.92:8080","remoteAddress":"/10.244.0.102:44630","method":"GET","path":"/path/to/resource","authority":"10.100.186.74:8080","headers":{"content-length":"0","upgrade":"h2c",":method":"GET",":scheme":"http","connection":"upgrade,http2-settings",":path":"/path/to/resource",":authority":"10.100.186.74:8080","user-agent":"Inverno/1.12.0-SNAPSHOT"}}
```

As for the previous example, requests should be load balanced among replicas:

```plaintext
$ curl -H 'host: example-discovery-http-testserver' http://127.0.0.1:37807/k8s-env/path/to/resource
{"localAddress":"/10.244.0.90:8080",...
$ curl -H 'host: example-discovery-http-testserver' http://127.0.0.1:37807/k8s-env/path/to/resource
{"localAddress":"/10.244.0.92:8080",...
$ curl -H 'host: example-discovery-http-testserver' http://127.0.0.1:37807/k8s-env/path/to/resource
{"localAddress":"/10.244.0.91:8080",...
```

This use case is actually quite similar to the first one, and you will need to wait between two requests for the connection to expire to see the load balancing in action. The only difference is that the service IP is directly obtained from the environment variables, there is no need to query the DNS server.

Finally, when resolving the headless service, the addresses of all three replicas are returned by the DNS server and requests can then be load balanced client side within the application:

```plaintext
curl -H 'host: example-discovery-http-testserver-headless:8080' http://127.0.0.1:37807/http/path/to/resource
{"localAddress":"/10.244.0.101:8080","remoteAddress":"/10.244.0.102:35924","method":"GET","path":"/path/to/resource","authority":"example-discovery-http-testserver-headless.default.svc.cluster.local:8080","headers":{"content-length":"0","upgrade":"h2c",":method":"GET",":scheme":"http","connection":"upgrade,http2-settings",":path":"/path/to/resource",":authority":"example-discovery-http-testserver-headless.default.svc.cluster.local:8080","user-agent":"Inverno/1.12.0-SNAPSHOT"}}
```

By default, the DNS discovery service uses a random load balancing strategy to distribute requests among the replicas, if we send the request a reasonable amount of times, we should see that the random load balancer in action:

```plaintext
$ curl -H 'host: example-discovery-http-testserver-headless:8080' http://127.0.0.1:37807/http/path/to/resource
{"localAddress":"/10.244.0.99:8080"...
$ curl -H 'host: example-discovery-http-testserver-headless:8080' http://127.0.0.1:37807/http/path/to/resource
{"localAddress":"/10.244.0.100:8080"...
$ curl -H 'host: example-discovery-http-testserver-headless:8080' http://127.0.0.1:37807/http/path/to/resource
{"localAddress":"/10.244.0.101:8080"...
```

In previous use cases, the built-in Kubernetes load balancer based on iptables rules was used and the application only created one connection to the service IP. In that case, the application create one connection per replica and load balance requests among these connections, we can see that requests are randomly sent to all replicas and we no longer have to wait for the connection expire.

## Building a native image

Using [GraalVM][graalvm], you can also build a native image of the application with the following command:

```plaintext
$ mvn clean package -Pnative
```

You can then run the native application:

```plaintext
$ ./target/example-discovery-http-k8s
2024-09-13 15:45:56,991 INFO  [main] i.i.c.v.Application - Inverno is starting...


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


2024-09-13 15:45:56,991 INFO  [main] i.i.e.a.App_discovery_http_k8s - Starting Module io.inverno.example.app_discovery_http_k8s...
2024-09-13 15:45:56,991 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2024-09-13 15:45:57,000 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 8ms
2024-09-13 15:45:57,000 INFO  [main] i.i.m.d.h.HTTP - Starting Module io.inverno.mod.discovery.http...
2024-09-13 15:45:57,000 INFO  [main] i.i.m.h.c.Client - Starting Module io.inverno.mod.http.client...
2024-09-13 15:45:57,000 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-09-13 15:45:57,000 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 0ms
2024-09-13 15:45:57,000 INFO  [main] i.i.m.h.c.Client - Module io.inverno.mod.http.client started in 0ms
2024-09-13 15:45:57,000 INFO  [main] i.i.m.d.h.HTTP - Module io.inverno.mod.discovery.http started in 0ms
2024-09-13 15:45:57,000 INFO  [main] i.i.m.d.h.k.K8s - Starting Module io.inverno.mod.discovery.http.k8s...
2024-09-13 15:45:57,000 INFO  [main] i.i.m.d.h.k.K8s - Module io.inverno.mod.discovery.http.k8s started in 0ms
2024-09-13 15:45:57,000 INFO  [main] i.i.m.w.c.Client - Starting Module io.inverno.mod.web.client...
2024-09-13 15:45:57,000 INFO  [main] i.i.m.w.b.Base - Starting Module io.inverno.mod.web.base...
2024-09-13 15:45:57,000 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-09-13 15:45:57,000 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 0ms
2024-09-13 15:45:57,000 INFO  [main] i.i.m.w.b.Base - Module io.inverno.mod.web.base started in 0ms
2024-09-13 15:45:57,001 INFO  [main] i.i.m.w.c.Client - Module io.inverno.mod.web.client started in 1ms
2024-09-13 15:45:57,001 INFO  [main] i.i.m.w.s.Server - Starting Module io.inverno.mod.web.server...
2024-09-13 15:45:57,001 INFO  [main] i.i.m.h.s.Server - Starting Module io.inverno.mod.http.server...
2024-09-13 15:45:57,001 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-09-13 15:45:57,001 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 0ms
2024-09-13 15:45:57,001 INFO  [main] i.i.m.w.b.Base - Starting Module io.inverno.mod.web.base...
2024-09-13 15:45:57,001 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2024-09-13 15:45:57,001 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 0ms
2024-09-13 15:45:57,001 INFO  [main] i.i.m.w.b.Base - Module io.inverno.mod.web.base started in 0ms
2024-09-13 15:45:57,003 INFO  [main] i.i.m.h.s.i.HttpServer - HTTP Server (epoll) listening on http://0.0.0.0:8080
2024-09-13 15:45:57,003 INFO  [main] i.i.m.h.s.Server - Module io.inverno.mod.http.server started in 1ms
2024-09-13 15:45:57,003 INFO  [main] i.i.m.w.s.Server - Module io.inverno.mod.web.server started in 1ms
2024-09-13 15:45:57,003 INFO  [main] i.i.e.a.App_discovery_http_k8s - Module io.inverno.example.app_discovery_http_k8s started in 12ms
2024-09-13 15:45:57,003 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_discovery_http_k8s started in 12ms
```

> Note that for the native image to work, [logback][logback] must be used as logging manager since log4j doesn't support native build (see https://issues.apache.org/jira/browse/LOG4J2-2649).

## Going further

- [Web client module documentation][inverno-mod-web-client]
- [Web server module documentation][inverno-mod-web-server]
- [Discovery API module documentation][inverno-mod-discovery]
- [Discovery HTTP module documentation][inverno-mod-discovery-http]
- [Discovery HTTP Metadata module documentation][inverno-mod-discovery-http-metadata]
- [Discovery HTTP Kubernetes module documentation][inverno-mod-discovery-http-k8s]
- [Inverno distribution documentation][inverno-dist-root]
- [Inverno Maven plugin documentation][inverno-tool-maven-plugin]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]
- [Kubernetes][kubernetes]
- [Minikube][minikube]