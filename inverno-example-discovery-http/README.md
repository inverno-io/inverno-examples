[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-dist-root]: https://github.com/inverno-io/inverno-dist
[inverno-tool-maven-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-maven-plugin
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[inverno-mod-discovery]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery/
[inverno-mod-discovery-http]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery-http/
[inverno-mod-discovery-http-meta]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery-http-meta/
[inverno-mod-discovery-http-k8s]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-mod-discovery-http-k8s/
[inverno-mod-http-client]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-http-client/

[inverno-examples-discovery-http-k8s]: ../inverno-example-discovery-http-k8s
[inverno-examples-discovery-http-testserver]: ../inverno-example-discovery-http-testserver

[epoll]: https://en.wikipedia.org/wiki/Epoll
[graalvm]: https://www.graalvm.org/
[logback]: https://logback.qos.ch/

# Inverno Discovery HTTP example

A sample application demonstrating how services can be resolved using basic [DNS][inverno-mod-discovery-http] or [Meta][inverno-mod-discovery-http-meta] Discovery services.

The application is a simple command line HTTP client that resolves services based on an input URI and send one or more requests to resulting [test server instances][inverno-examples-discovery-http-testserver] running locally on different ports.

It demonstrates several features such as:
- request load balancing
- content based routing
- request rewrite
- per route traffic policy
- per destination traffic policy
- ...

The HTTP client configuration is exposed in the module's configuration `AppConfiguration`.

The client is configured to use [epoll][epoll] when available (i.e. on Linux platform) for better performance.

## Running the application

The application is started using the Inverno Maven plugin as follows:

```plaintext
$ mvn inverno:run -Dinverno.run.arguments="-h"
...
Usage: example-discovery-http [-hV] [-auth=<authority>] [-c=<count>]
                              [-H=<String=String>]... uri
Inverno Discovery HTTP example
      uri               The resource URI
      -auth, --authority=<authority>
                        Set the request authority
  -c, --count=<count>   Set the number of requests to send
  -h, --help            Show this help message and exit.
  -H, --header=<String=String>
                        Add a request header
  -V, --version         Print version information and exit.
```

But it is more convenient to run it with the native launcher generated when packaging the application (see [Packaging the application](#packaging-the-application)).

Assuming three [test server instances][inverno-examples-discovery-http-testserver] have been started locally on port 8080, 8081 and 8082, you can now test several use cases

### DNS lookup

The most basic service discovery is done by querying a Domain Name Service to resolve the IP address(es) mapped to a given host name. Let's add an entry to the `hosts` file for testing purposes:

```plaintext
127.0.0.1    test-service
```

We can now try to query test servers using `test-service` host name:

```plaintext
$ ./example-discovery-http http://test-service:8081/path/to/resource
...
2024-09-13 16:20:07,263 INFO  [inverno-io-epoll-1-2] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8081', remoteAddress='/127.0.0.1:46384', method='GET', path='/path/to/resource', authority='test-service:8081', headers={content-length=0, upgrade=h2c, :method=GET, :scheme=http, connection=upgrade,http2-settings, :path=/path/to/resource, :authority=test-service:8081, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=349, content-type=application/json, :status=200}}
2024-09-13 16:20:07,263 INFO  [inverno-io-epoll-1-2] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:20:07,264 INFO  [inverno-io-epoll-1-2] i.i.e.a.Main - 1 request(s) ----> /127.0.0.1:8081
2024-09-13 16:20:07,264 INFO  [inverno-io-epoll-1-2] i.i.e.a.Main - --------------------------------------------------------------------------------
...
```

The DNS `DiscoveryService` bean was invoke to resolve `test-service:8081` to an inet socket address, `127.0.0.1:8081` in our case. A DNS server may return multiple IP addresses when the host name is mapped to multiple addresses, the resulting resolved service then distributes request among them using `RANDOM` (default) or `ROUND_ROBIN` load balancing strategies.

> Note that port can't be resolved with a DNS lookup and must still be known to the client.

### Simple Configuration

The HTTP meta discovery service looks for meta service descriptors defined in a `ConfigurationSource`. Meta HTTP services provide rich and advanced features that goes from a simple service name to address mapping to advanced content based routing, load balancing, request rewriting...

In its most simple form, a service name can be mapped to an inet socket address in a configuration source (e.g. configuration file, Redis data store...). The sample application provides file `src/main/resource/configuration.cprops` which contains several meta HTTP service descriptors, for instance:

```plaintext
io.inverno.mod.discovery.http.meta.service.singleDestination = "http://127.0.0.1:8080"
...
```

Service `singleDestination` can then be identified in `conf://singleDestination` URI.

```plaintext
$ ./example-discovery-http conf://singleDestination/path/to/resource
...
2024-09-13 16:36:28,611 INFO  [main] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:36:28,760 INFO  [inverno-io-epoll-1-2] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8080
2024-09-13 16:36:28,974 INFO  [inverno-io-epoll-1-2] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8080', remoteAddress='/127.0.0.1:38642', method='GET', path='/path/to/resource', authority='127.0.0.1:8080', headers={content-length=0, upgrade=h2c, :method=GET, :scheme=http, connection=upgrade,http2-settings, :path=/path/to/resource, :authority=127.0.0.1:8080, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=357, content-type=application/json, :status=200}}
2024-09-13 16:36:28,974 INFO  [inverno-io-epoll-1-2] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:36:28,975 INFO  [inverno-io-epoll-1-2] i.i.e.a.Main - 1 request(s) ----> /127.0.0.1:8080
2024-09-13 16:36:28,975 INFO  [inverno-io-epoll-1-2] i.i.e.a.Main - --------------------------------------------------------------------------------
...
```

### Per destination configuration

When resolving a service, the discovery service eventually create an HTTP client `Endpoint` using the configuration provided in the HTTP client module, this can be overridden per destination as for instance:

```plaintext
io.inverno.mod.discovery.http.meta.service.singleDestinationWithConfig = """[{"uri":"http://127.0.0.1:8080","configuration":{"user_agent":"Discovery example client"}}]"""
...
```

When requesting service `singleDestinationWithConfig`, the user agent sent to the server is now set to `Discovery example client`:

```plaintext
$ ./example-discovery-http conf://singleDestinationWithConfig/path/to/resource
...
2024-09-13 16:39:04,908 INFO  [main] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:39:05,164 INFO  [inverno-io-epoll-1-2] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8080
2024-09-13 16:39:05,240 INFO  [inverno-io-epoll-1-2] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8080', remoteAddress='/127.0.0.1:49872', method='GET', path='/path/to/resource', authority='127.0.0.1:8080', headers={content-length=0, upgrade=h2c, :method=GET, :scheme=http, connection=upgrade,http2-settings, :path=/path/to/resource, :authority=127.0.0.1:8080, user-agent=Discovery example client}, responseHeaders={content-length=358, content-type=application/json, :status=200}}
2024-09-13 16:39:05,240 INFO  [inverno-io-epoll-1-2] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:39:05,241 INFO  [inverno-io-epoll-1-2] i.i.e.a.Main - 1 request(s) ----> /127.0.0.1:8080
2024-09-13 16:39:05,241 INFO  [inverno-io-epoll-1-2] i.i.e.a.Main - --------------------------------------------------------------------------------
...
```

> Many configuration settings can be overridden in the service descriptor per route or per destination.

### Random load balancing on multiple destinations

When multiple destinations are mapped to a single service name, the discovery service creates a load balancer to distribute traffic to the resolved `Endpoint` using a `RANDOM` load balancing strategy by default.

```plaintext
io.inverno.mod.discovery.http.meta.service.multiDestinationRandom = """["http://127.0.0.1:8080", "http://127.0.0.1:8081", "http://127.0.0.1:8082"]"""
```

If you request the `multiDestinationRandom` several times, you should see the requests being distributed evenly among the three test server instances:

```plaintext
$ ./example-discovery-http conf://multiDestinationRandom/path/to/resource -c 100
...
2024-09-13 16:44:23,884 INFO  [main] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:44:24,049 INFO  [inverno-io-epoll-1-6] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8082
2024-09-13 16:44:24,049 INFO  [inverno-io-epoll-1-5] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8080
2024-09-13 16:44:24,049 INFO  [inverno-io-epoll-1-4] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8081
2024-09-13 16:44:24,252 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8080', remoteAddress='/127.0.0.1:44790', method='GET', path='/path/to/resource', authority='127.0.0.1:8080', headers={content-length=0, upgrade=h2c, :method=GET, :scheme=http, connection=upgrade,http2-settings, :path=/path/to/resource, :authority=127.0.0.1:8080, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=357, content-type=application/json, :status=200}}
...
2024-09-13 16:44:24,269 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8082', remoteAddress='/127.0.0.1:42036', method='GET', path='/path/to/resource', authority='127.0.0.1:8082', headers={:method=GET, :scheme=http, :path=/path/to/resource, :authority=127.0.0.1:8082, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=282, content-type=application/json, :status=200}}
...
2024-09-13 16:44:24,291 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8081', remoteAddress='/127.0.0.1:52814', method='GET', path='/path/to/resource', authority='127.0.0.1:8081', headers={:method=GET, :scheme=http, :path=/path/to/resource, :authority=127.0.0.1:8081, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=282, content-type=application/json, :status=200}}
...
2024-09-13 16:44:24,299 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8080', remoteAddress='/127.0.0.1:44790', method='GET', path='/path/to/resource', authority='127.0.0.1:8080', headers={:method=GET, :scheme=http, :path=/path/to/resource, :authority=127.0.0.1:8080, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=282, content-type=application/json, :status=200}}
2024-09-13 16:44:24,299 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:44:24,300 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main -  32 request(s) ----> /127.0.0.1:8082
2024-09-13 16:44:24,300 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main -  35 request(s) ----> /127.0.0.1:8081
2024-09-13 16:44:24,300 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main -  33 request(s) ----> /127.0.0.1:8080
2024-09-13 16:44:24,300 INFO  [inverno-io-epoll-1-5] i.i.e.a.Main - --------------------------------------------------------------------------------
...
```

### Weighted RoundRobin load balancing on multiple destinations

The load balancing strategy can be configured in the service descriptor, per route or globally (i.e. the same strategy applies to all routes defined in the service), weights can also be specified on each destination.

```plaintext
io.inverno.mod.discovery.http.meta.service.multiDestinationWeightedRoundRobin = """
{
    "routes": [
        {
            "loadBalancer": {
                "strategy": "ROUND_ROBIN"
            },
            "destinations":[
                {"uri":"http://127.0.0.1:8080", "weight":80},
                {"uri":"http://127.0.0.1:8081", "weight":10},
                {"uri":"http://127.0.0.1:8082", "weight":10}
            ]
        }
    ]
}
"""
```

The `ROUND_ROBIN` strategy being more deterministic than the `RANDOM` strategy, with above configuration, sending 10 requests should result in 8 requests being sent to server port 8080, and 1 to server 8081 and server 8082 respectively.

```plaintext
$ ./example-discovery-http conf://multiDestinationWeightedRoundRobin/path/to/resource -count 10
2024-09-13 16:51:49,876 INFO  [main] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:51:50,089 INFO  [inverno-io-epoll-1-6] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8082
2024-09-13 16:51:50,089 INFO  [inverno-io-epoll-1-4] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8080
2024-09-13 16:51:50,089 INFO  [inverno-io-epoll-1-5] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8081
2024-09-13 16:51:50,222 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8080', remoteAddress='/127.0.0.1:37492', method='GET', path='/path/to/resource', authority='127.0.0.1:8080', headers={content-length=0, upgrade=h2c, :method=GET, :scheme=http, connection=upgrade,http2-settings, :path=/path/to/resource, :authority=127.0.0.1:8080, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=357, content-type=application/json, :status=200}}
2024-09-13 16:51:50,222 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8081', remoteAddress='/127.0.0.1:34100', method='GET', path='/path/to/resource', authority='127.0.0.1:8081', headers={content-length=0, upgrade=h2c, :method=GET, :scheme=http, connection=upgrade,http2-settings, :path=/path/to/resource, :authority=127.0.0.1:8081, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=357, content-type=application/json, :status=200}}
2024-09-13 16:51:50,222 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8082', remoteAddress='/127.0.0.1:53624', method='GET', path='/path/to/resource', authority='127.0.0.1:8082', headers={content-length=0, upgrade=h2c, :method=GET, :scheme=http, connection=upgrade,http2-settings, :path=/path/to/resource, :authority=127.0.0.1:8082, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=357, content-type=application/json, :status=200}}
...
2024-09-13 16:51:50,229 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:51:50,231 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main -  1 request(s) ----> /127.0.0.1:8082
2024-09-13 16:51:50,231 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main -  1 request(s) ----> /127.0.0.1:8081
2024-09-13 16:51:50,231 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main -  8 request(s) ----> /127.0.0.1:8080
2024-09-13 16:51:50,231 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
...
```

### Path based routing

It is possible to route a request to a specific route based on the request path. Each route defines its own set of destinations.

```plaintext
io.inverno.mod.discovery.http.meta.service.multiDestinationRoutingPath = """
{
    "routes": [
        {
            "path": [
                {"path":"/service1/**"}
            ],
            "destinations":[
                {"uri":"http://127.0.0.1:8081"}
            ]
        },
        {
            "path": [
                {"path":"/service2/**"}
            ],
            "destinations":[
                {"uri":"http://127.0.0.1:8082"}
            ]
        },
        {
            "destinations":[
                {"uri":"http://127.0.0.1:8080"}
            ]
        }
    ]
}
"""
```

With above configuration, a request to `/service1/path/to/resource` would be served by server 8081, a request to `/service2/path/to/resource` by server 8082 and any other request by server 8080.

```plaintext
$ ./example-discovery-http conf://multiDestinationRoutingPath/path/to/resource
...
2024-09-13 16:58:36,829 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:58:36,831 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - 1 request(s) ----> /127.0.0.1:8080
2024-09-13 16:58:36,832 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
...
$ ./example-discovery-http conf://multiDestinationRoutingPath/service1/path/to/resource
...
2024-09-13 16:58:36,829 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:58:36,831 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - 1 request(s) ----> /127.0.0.1:8081
2024-09-13 16:58:36,832 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
...
$ ./example-discovery-http conf://multiDestinationRoutingPath/service2/path/to/resource
...
2024-09-13 16:58:36,829 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:58:36,831 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - 1 request(s) ----> /127.0.0.1:8082
2024-09-13 16:58:36,832 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
...
```

### Authority based routing

It is actually possible to route requests based on any request content including: the path, method, the authority, headers, query parameters, content type, accepted content types and accepted languages.

For instance, in order to route request based on the request authority (`host` or `:authority` headers).

```plaintext
io.inverno.mod.discovery.http.meta.service.multiDestinationRoutingAuthority = """
{
    "routes": [
        {
            "authority": [{"value":"service-1"}],
            "destinations":[
                {"uri":"http://127.0.0.1:8081"}
            ]
        },
        {
            "authority": [{"value":"service-2"}],
            "destinations":[
                {"uri":"http://127.0.0.1:8082"}
            ]
        },
        {
            "destinations":[
                {"uri":"http://127.0.0.1:8080"}
            ]
        }
    ]
}
```

Routing criteria can be combined, a request is matching a route when it matches all the criteria defined in the route.

> That is not entirely accurate, in order to make sure a request is always mapped to a route, the algorithm actually prioritizes the criteria in a similar way as for Web server routes. Please consult the Inverno Discovery HTTP meta module [documentation][inverno-mod-discovery-http-meta] for more details on this.

Replacing the path in previous example with the request authority produce the same kind of results:

```plaintext
$ ./example-discovery-http conf://multiDestinationRoutingAuthority/path/to/resource
...
2024-09-13 16:58:36,829 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:58:36,831 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - 1 request(s) ----> /127.0.0.1:8080
2024-09-13 16:58:36,832 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
...
$ ./example-discovery-http conf://multiDestinationRoutingAuthority/path/to/resource -auth service-1
...
2024-09-13 16:58:36,829 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:58:36,831 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - 1 request(s) ----> /127.0.0.1:8081
2024-09-13 16:58:36,832 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
...
$ ./example-discovery-http conf://multiDestinationRoutingAuthority/path/to/resource -auth service-2
...
2024-09-13 16:58:36,829 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 16:58:36,831 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - 1 request(s) ----> /127.0.0.1:8082
2024-09-13 16:58:36,832 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
...
```

### Path rewrite

A request can be rewritten per route or per destination, the following example shows how to rewrite the path. 

```plaintext
io.inverno.mod.discovery.http.meta.service.multiDestinationRewritePath = """
{
    "routes": [
        {
            "path": [
                {"path":"/v1/**"}
            ],
            "transformRequest": {
                "translatePath":{
                    "/{version}/{path:**}":"/{path:**}"
                }
            },
            "destinations":[
                {"uri":"http://127.0.0.1:8081"}
            ]
        },
        {
            "path": [
                {"path":"/v2/**"}
            ],
            "transformRequest": {
                "translatePath":{
                    "/{version}/{path:**}":"/{path:**}"
                }
            },
            "destinations":[
                {"uri":"http://127.0.0.1:8082"}
            ]
        },
        {
            "destinations":[
                {"uri":"http://127.0.0.1:8080"}
            ]
        }
    ]
}
```

Considering above configuration, `/v1/**` requests will be routed to server 8081 and the path will be rewritten from `/v1/**` to `/**` and `/v2/**` routed to server 8082 with the version segment removed from the path, any other request will be routed to server 8080 with an untouched path.

```plaintext
$ ./example-discovery-http conf://multiDestinationRewritePath/path/to/resource
...
2024-09-13 17:15:13,478 INFO  [main] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 17:15:13,699 INFO  [inverno-io-epoll-1-4] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8080
2024-09-13 17:15:13,773 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8080', remoteAddress='/127.0.0.1:46854', method='GET', path='/path/to/resource', authority='127.0.0.1:8080', headers={content-length=0, upgrade=h2c, :method=GET, :scheme=http, connection=upgrade,http2-settings, :path=/path/to/resource, :authority=127.0.0.1:8080, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=357, content-type=application/json, :status=200}}
2024-09-13 17:15:13,773 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 17:15:13,774 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - 1 request(s) ----> /127.0.0.1:8080
2024-09-13 17:15:13,774 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
...
$ ./example-discovery-http conf://multiDestinationRewritePath/v1/path/to/resource
...
2024-09-13 17:15:30,967 INFO  [main] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 17:15:31,213 INFO  [inverno-io-epoll-1-4] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8081
2024-09-13 17:15:31,281 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8081', remoteAddress='/127.0.0.1:41238', method='GET', path='/path/to/resource', authority='127.0.0.1:8081', headers={content-length=0, upgrade=h2c, :method=GET, :scheme=http, connection=upgrade,http2-settings, :path=/path/to/resource, :authority=127.0.0.1:8081, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=357, content-type=application/json, :status=200}}
2024-09-13 17:15:31,281 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 17:15:31,282 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - 1 request(s) ----> /127.0.0.1:8081
2024-09-13 17:15:31,282 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
...
$ ./example-discovery-http conf://multiDestinationRewritePath/v2/path/to/resource
...
2024-09-13 17:15:41,862 INFO  [main] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 17:15:42,133 INFO  [inverno-io-epoll-1-4] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8082
2024-09-13 17:15:42,210 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8082', remoteAddress='/127.0.0.1:45804', method='GET', path='/path/to/resource', authority='127.0.0.1:8082', headers={content-length=0, upgrade=h2c, :method=GET, :scheme=http, connection=upgrade,http2-settings, :path=/path/to/resource, :authority=127.0.0.1:8082, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=357, content-type=application/json, :status=200}}
2024-09-13 17:15:42,210 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 17:15:42,211 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - 1 request(s) ----> /127.0.0.1:8082
2024-09-13 17:15:42,211 INFO  [inverno-io-epoll-1-4] i.i.e.a.Main - --------------------------------------------------------------------------------
...
```

### Headers rewrite

It is actually possible to rewrite the request path, the request authority and set/add/remove request or response headers.

```plaintext
io.inverno.mod.discovery.http.meta.service.multiDestinationSetHeaders = """
{
    "routes": [
        {
            "loadBalancer": {
                "strategy": "ROUND_ROBIN"
            },
            "transformRequest": {
                "setHeaders":{"route-header":"abc"}
            },
            "destinations":[
                {"uri":"http://127.0.0.1:8080"},
                {
                    "uri":"http://127.0.0.1:8081",
                    "transformResponse": {
                        "setHeaders":{"destination-header":"service-1"}
                    }
                },
                {
                    "uri":"http://127.0.0.1:8082",
                    "transformResponse": {
                        "setHeaders":{"destination-header":"service-2"}
                    }
                }
            ]
        }
    ]
}
```

Considering above configuration, requests are evenly load balanced among the three server instances, `route-header` header is set on all request whereas `destination-header` is only set, with different values, when the request is routed to server 8081 and server 8082.

```plaintext
$ ./example-discovery-http conf://multiDestinationSetHeaders/path/to/resource -c 9
...
2024-09-13 17:20:00,851 INFO  [main] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 17:20:01,085 INFO  [inverno-io-epoll-1-5] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8080
2024-09-13 17:20:01,085 INFO  [inverno-io-epoll-1-4] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8082
2024-09-13 17:20:01,085 INFO  [inverno-io-epoll-1-6] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8081
2024-09-13 17:20:01,215 INFO  [inverno-io-epoll-1-6] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8081', remoteAddress='/127.0.0.1:33168', method='GET', path='/path/to/resource', authority='127.0.0.1:8081', headers={content-length=0, upgrade=h2c, :method=GET, :scheme=http, connection=upgrade,http2-settings, :path=/path/to/resource, route-header=abc, :authority=127.0.0.1:8081, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=378, content-type=application/json, :status=200, destination-header=service-1}}
2024-09-13 17:20:01,215 INFO  [inverno-io-epoll-1-6] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8082', remoteAddress='/127.0.0.1:60028', method='GET', path='/path/to/resource', authority='127.0.0.1:8082', headers={content-length=0, upgrade=h2c, :method=GET, :scheme=http, connection=upgrade,http2-settings, :path=/path/to/resource, route-header=abc, :authority=127.0.0.1:8082, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=378, content-type=application/json, :status=200, destination-header=service-2}}
2024-09-13 17:20:01,215 INFO  [inverno-io-epoll-1-6] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8080', remoteAddress='/127.0.0.1:51648', method='GET', path='/path/to/resource', authority='127.0.0.1:8080', headers={content-length=0, upgrade=h2c, :method=GET, :scheme=http, connection=upgrade,http2-settings, :path=/path/to/resource, route-header=abc, :authority=127.0.0.1:8080, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=378, content-type=application/json, :status=200}}
2024-09-13 17:20:01,216 INFO  [inverno-io-epoll-1-6] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8082', remoteAddress='/127.0.0.1:60028', method='GET', path='/path/to/resource', authority='127.0.0.1:8082', headers={:method=GET, :scheme=http, :path=/path/to/resource, route-header=abc, :authority=127.0.0.1:8082, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=303, content-type=application/json, :status=200, destination-header=service-2}}
2024-09-13 17:20:01,216 INFO  [inverno-io-epoll-1-6] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8080', remoteAddress='/127.0.0.1:51648', method='GET', path='/path/to/resource', authority='127.0.0.1:8080', headers={:method=GET, :scheme=http, :path=/path/to/resource, route-header=abc, :authority=127.0.0.1:8080, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=303, content-type=application/json, :status=200}}
2024-09-13 17:20:01,216 INFO  [inverno-io-epoll-1-6] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8082', remoteAddress='/127.0.0.1:60028', method='GET', path='/path/to/resource', authority='127.0.0.1:8082', headers={:method=GET, :scheme=http, :path=/path/to/resource, route-header=abc, :authority=127.0.0.1:8082, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=303, content-type=application/json, :status=200, destination-header=service-2}}
2024-09-13 17:20:01,216 INFO  [inverno-io-epoll-1-6] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8080', remoteAddress='/127.0.0.1:51648', method='GET', path='/path/to/resource', authority='127.0.0.1:8080', headers={:method=GET, :scheme=http, :path=/path/to/resource, route-header=abc, :authority=127.0.0.1:8080, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=303, content-type=application/json, :status=200}}
2024-09-13 17:20:01,217 INFO  [inverno-io-epoll-1-6] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8081', remoteAddress='/127.0.0.1:33168', method='GET', path='/path/to/resource', authority='127.0.0.1:8081', headers={:method=GET, :scheme=http, :path=/path/to/resource, route-header=abc, :authority=127.0.0.1:8081, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=303, content-type=application/json, :status=200, destination-header=service-1}}
2024-09-13 17:20:01,219 INFO  [inverno-io-epoll-1-6] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8081', remoteAddress='/127.0.0.1:33168', method='GET', path='/path/to/resource', authority='127.0.0.1:8081', headers={:method=GET, :scheme=http, :path=/path/to/resource, route-header=abc, :authority=127.0.0.1:8081, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=303, content-type=application/json, :status=200, destination-header=service-1}}
2024-09-13 17:20:01,219 INFO  [inverno-io-epoll-1-6] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 17:20:01,220 INFO  [inverno-io-epoll-1-6] i.i.e.a.Main - 3 request(s) ----> /127.0.0.1:8082
2024-09-13 17:20:01,220 INFO  [inverno-io-epoll-1-6] i.i.e.a.Main - 3 request(s) ----> /127.0.0.1:8081
2024-09-13 17:20:01,220 INFO  [inverno-io-epoll-1-6] i.i.e.a.Main - 3 request(s) ----> /127.0.0.1:8080
2024-09-13 17:20:01,220 INFO  [inverno-io-epoll-1-6] i.i.e.a.Main - --------------------------------------------------------------------------------
```

### Missing default route

It is a good practice to always define a default route (i.e. a route with no criteria) but this is not obligatory.

```plaintext
io.inverno.mod.discovery.http.meta.service.testNoDefault = """
{
    "routes": [
        {
            "path": [
                {"path":"/service1/**"}
            ],
            "destinations":[
                {"uri":"http://127.0.0.1:8081"}
            ]
        },
        {
            "path": [
                {"path":"/service2/**"}
            ],
            "destinations":[
                {"uri":"http://127.0.0.1:8082"}
            ]
        }
    ]
}
"""
```

In that particular case, sending a request that doesn't match the above path criteria will result in an unresolved service instance (i.e. no route matches the request), the user is then free to decide what to do. In the application a `ServiceNotFoundException` is raised when an empty service instance is returned:


```java
return service.getInstance(exchange).switchIfEmpty(Mono.error(() -> new ServiceNotFoundException(serviceId, "No matching instance")))...
```

```plaintext
$ ./example-discovery-http conf://testNoDefault/service1/path/to/resource
...
2024-09-13 17:24:48,280 INFO  [main] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 17:24:48,511 INFO  [inverno-io-epoll-1-3] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://127.0.0.1:8081
2024-09-13 17:24:48,591 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - TestServerResponse{localAddress='/127.0.0.1:8081', remoteAddress='/127.0.0.1:58066', method='GET', path='/service1/path/to/resource', authority='127.0.0.1:8081', headers={content-length=0, upgrade=h2c, :method=GET, :scheme=http, connection=upgrade,http2-settings, :path=/service1/path/to/resource, :authority=127.0.0.1:8081, user-agent=Inverno/1.12.0-SNAPSHOT}, responseHeaders={content-length=375, content-type=application/json, :status=200}}
2024-09-13 17:24:48,591 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - --------------------------------------------------------------------------------
2024-09-13 17:24:48,592 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - 1 request(s) ----> /127.0.0.1:8081
2024-09-13 17:24:48,592 INFO  [inverno-io-epoll-1-3] i.i.e.a.Main - --------------------------------------------------------------------------------
...
$ ./example-discovery-http conf://testNoDefault/path/to/resource
...
Exception in thread "main" io.inverno.mod.discovery.ServiceNotFoundException: No matching instance
```

## Packaging the application

The application can be packaged as a native runtime image by invoking the `release` build profile:

```plaintext
$ mvn install -Prelease
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:package-app (inverno-package-app) @ inverno-example-discovery-http ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] Project application archives created: zip
[INFO] 
[INFO] --- install:3.1.1:install (default-install) @ inverno-example-discovery-http ---
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http/1.0.0-SNAPSHOT/inverno-example-discovery-http-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http/target/inverno-example-discovery-http-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http/1.0.0-SNAPSHOT/inverno-example-discovery-http-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/winter/inverno-examples/inverno-example-discovery-http/target/inverno-example-discovery-http-1.0.0-SNAPSHOT-application_linux_amd64.zip to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-discovery-http/1.0.0-SNAPSHOT/inverno-example-discovery-http-1.0.0-SNAPSHOT-application_linux_amd64.zip
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The previous command creates folder `target/inverno-example-discovery-http-1.0.0-SNAPSHOT-application_linux_amd64` containing the Java runtime and the application and installed the corresponding archive to the Maven repository:

The application can now be started with the following native command:

```plaintext
$ ../target/inverno-example-discovery-http-1.0.0-SNAPSHOT-application_linux_amd64/bin/example-discovery-http
...
```

## Building a native image

Using [GraalVM][graalvm], you can also build a native image of the application with the following command:

```plaintext
$ mvn clean package -Pnative
```

You can then run the native application:

```plaintext
$ ./target/example-discovery-http -h
Usage: example-discovery-http [-hV] [-auth=<authority>] [-c=<count>]
                              [-H=<String=String>]... uri
Inverno Discovery HTTP example
      uri               The resource URI
      -auth, --authority=<authority>
                        Set the request authority
  -c, --count=<count>   Set the number of requests to send
  -h, --help            Show this help message and exit.
  -H, --header=<String=String>
                        Add a request header
  -V, --version         Print version information and exit.

```

> Note that for the native image to work, [logback][logback] must be used as logging manager since log4j doesn't support native build (see https://issues.apache.org/jira/browse/LOG4J2-2649).

## Going further

- [HTTP client module documentation][inverno-mod-http-client]
- [Discovery API module documentation][inverno-mod-discovery]
- [Discovery HTTP module documentation][inverno-mod-discovery-http]
- [Discovery HTTP Metadata module documentation][inverno-mod-discovery-http-meta]
- [Discovery HTTP Kubernetes module documentation][inverno-mod-discovery-http-k8s]
- [Inverno distribution documentation][inverno-dist-root]
- [Inverno Maven plugin documentation][inverno-tool-maven-plugin]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]