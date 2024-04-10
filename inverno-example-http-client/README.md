[inverno-mod-http-client]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-http-client/
[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html
[inverno-example-http]: https://github.com/inverno-io/inverno-examples/blob/master/inverno-example-http/
[inverno-example-web]: https://github.com/inverno-io/inverno-examples/blob/master/inverno-example-web/

[epoll]: https://en.wikipedia.org/wiki/Epoll
[picocli]: https://picocli.info/
[jline3]: https://github.com/jline/jline3
[sni]: https://en.wikipedia.org/wiki/Server_Name_Indication
[mTLS]: https://en.wikipedia.org/wiki/Mutual_authentication

# Inverno HTTP client example

A sample application showing how to use the HTTP client module to create efficient and performant HTTP/1.x and HTTP/2 clients.

The application is an HTTP shell that allows to connect to an HTTP server using HTTP/1.x or HTTP/2 with or without TLS and send HTTP requests.

The HTTP client configuration is exposed in the module's configuration `App_http_clientConfiguration`, a base configuration can be specified from the command line or in a `configuration.cprops` file. This configuration can be overridden at runtime from the HTTP shell using the `config` command.

The application uses [Picocli][picocli] and [Jline3][jline3] for the shell interface and it is configured to use [epoll][epoll] when available (ie. on Linux platform) for better performance.

The Maven build descriptor also defines a `release` profile which builds a native application image in a `zip` archive.

## Running the application

```plaintext
$ mvn inverno:run
2023-12-20 21:26:00,064 INFO  [main] i.i.c.v.Application - Inverno is starting...


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
     ║ Java version        : 21.0.1+12-29                                                         ║
     ║ Java home           : /home/jkuhn/Devel/jdk/jdk-21.0.1                                     ║
     ║                                                                                            ║
     ║ Application module  : io.inverno.example.app_http_client                                   ║
     ║ Application version : 1.0.0-SNAPSHOT                                                       ║
     ║ Application class   : io.inverno.example.app_http_client.Main                              ║
     ║                                                                                            ║
     ║ Modules             :                                                                      ║
     ║  * ...                                                                                     ║
     ╚════════════════════════════════════════════════════════════════════════════════════════════╝


2023-12-20 21:26:00,076 INFO  [main] i.i.e.a.App_http_client - Starting Module io.inverno.example.app_http_client...
2023-12-20 21:26:00,077 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2023-12-20 21:26:00,467 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 390ms
2023-12-20 21:26:00,467 INFO  [main] i.i.m.h.c.Client - Starting Module io.inverno.mod.http.client...
2023-12-20 21:26:00,467 INFO  [main] i.i.m.h.b.Base - Starting Module io.inverno.mod.http.base...
2023-12-20 21:26:00,471 INFO  [main] i.i.m.h.b.Base - Module io.inverno.mod.http.base started in 4ms
2023-12-20 21:26:00,475 INFO  [main] i.i.m.h.c.Client - Module io.inverno.mod.http.client started in 7ms
2023-12-20 21:26:00,476 INFO  [main] i.i.e.a.App_http_client - Module io.inverno.example.app_http_client started in 409ms
2023-12-20 21:26:00,477 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_http_client started in 459ms
> 
────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
```

The list of commands available in the shell can be obtained with the `help` command:

```
> help
  System:
    exit        exit from app/script
    help        command help
  Builtins:
    colors      view 256-color table and ANSI-styles
    highlighter manage nanorc theme system
    history     list history of commands
    keymap      manipulate keymaps
    less        file pager
    nano        edit files
    setopt      set options
    setvar      set lineReader variable value
    ttop        display and update sorted information about threads
    unsetopt    unset options
    widget      manipulate widgets
  Shell commands:
    clear       Clear screen
  Client commands:
    close       Close the Http endpoint
    config      Manage Http Client configuration properties
    open        Connect to an Http endpoint
  Http commands:
    delete      Send a DELETE HTTP request
    get         Send a GET HTTP request
    head        Send a HEAD HTTP request
    patch       Send a PATCH HTTP request
    post        Send a POST HTTP request
    put         Send a PUT HTTP request
> 
────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
```

Thanks to [jline3][jline3], the shell also supports autocompletion, pressing `Tab` can be used to autocomplete commands:

```
> clear
clear         colors        delete        get           help          history       less          open          post          setopt        ttop          widget
close         config        exit          head          highlighter   keymap        nano          patch         put           setvar        unsetopt
────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
```

When a valid command is entered, documentation is also automatically displayed in the section at the bottom of the screen: 

```
> open

────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
open [-hstV] [-p=<httpVersions>]... host [port]
```

Documentation for options and arguments is also provided:

```
> open -

────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
-V --version                  Print version information and exit.
-h --help                     Show this help message and exit.
-p --protocol=<httpVersions>  HTTP protocol version to use
-s --tls-enabled              Enable TLS
-t --tls-trust-all            Trust all certificates (insecure)
```

In order to send HTTP requests, you need first to open a connection to a server:

```
> open example.org
Connected to example.org/<unresolved>:80
example.org:80>
```

You can now send HTTP requests:

```
example.org:80> get /
2023-12-20 21:51:10,563 INFO  [inverno-io-epoll-1-5] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://example.org:80
> GET / HTTP/1.1
> user-agent: Inverno/1.6.0-SNAPSHOT
> host: example.org

────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
< HTTP/1.1 200 OK
< Accept-Ranges: bytes
< Age: 546848
< Cache-Control: max-age=604800
< Content-Type: text/html; charset=UTF-8
< Date: Wed, 20 Dec 2023 20:51:10 GMT
< Etag: "3147526947"
< Expires: Wed, 27 Dec 2023 20:51:10 GMT
< Last-Modified: Thu, 17 Oct 2019 07:18:26 GMT
< Server: ECS (dce/26CD)
< Vary: Accept-Encoding
< X-Cache: HIT
< Content-Length: 1256

<!doctype html>
<html>
<head>
    <title>Example Domain</title>

    <meta charset="utf-8" />
    <meta http-equiv="Content-type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <style type="text/css">
    body {
        background-color: #f0f0f2;
        margin: 0;
        padding: 0;
        font-family: -apple-system, system-ui, BlinkMacSystemFont, "Segoe UI", "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
        
    }
    div {
        width: 600px;
        margin: 5em auto;
        padding: 2em;
        background-color: #fdfdff;
        border-radius: 0.5em;
        box-shadow: 2px 3px 7px 2px rgba(0,0,0,0.02);
    }
    a:link, a:visited {
        color: #38488f;
        text-decoration: none;
    }
    @media (max-width: 700px) {
        div {
            margin: 0 auto;
            width: auto;
        }
    }
    </style>    
</head>

<body>
<div>
    <h1>Example Domain</h1>
    <p>This domain is for use in illustrative examples in documents. You may use this
    domain in literature without prior coordination or asking for permission.</p>
    <p><a href="https://www.iana.org/domains/example">More information...</a></p>
</div>
</body>
</html>

example.org:80> 
```

You can obtain help for any command with the `-h` option:

```
example.org:80> get -h
Usage:  get [-hV] [-a=<accept>] [-ae=<acceptEncoding>] [-al=<acceptLanguage>]
            [-H=<String=String>]... path
Send a GET HTTP request
      path                The path to the resource
  -a, --accept=<accept>   Set the accept header
      -ae, --accept-encoding=<acceptEncoding>
                          Set the accept-encoding header
      -al, --accept-language=<acceptLanguage>
                          Set the accept-language header
  -h, --help              Show this help message and exit.
  -H, --header=<String=String>
                          Header to include in the request when sending HTTP to
                            a server
  -V, --version           Print version information and exit.
example.org:80> 
```

For instance, you can set HTTP headers such as `accept` or any custom header as follows:

```
example.org:80> get -a text/plain -H some_header=123 /not_found
2023-12-20 22:01:25,264 INFO  [inverno-io-epoll-1-7] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://example.org:80
> GET /not_found HTTP/1.1
> user-agent: Inverno/1.6.0-SNAPSHOT
> some_header: 123
> accept: text/plain
> host: example.org

────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
< HTTP/1.1 404 Not Found
< Accept-Ranges: bytes
< Age: 109712
< Cache-Control: max-age=604800
< Content-Type: text/html; charset=UTF-8
< Date: Wed, 20 Dec 2023 21:01:25 GMT
< Expires: Wed, 27 Dec 2023 21:01:25 GMT
< Last-Modified: Tue, 19 Dec 2023 14:32:53 GMT
< Server: ECS (laa/7BB2)
< Vary: Accept-Encoding
< X-Cache: 404-HIT
< Content-Length: 1256

...
```

The Http protocol version can be specified in the configuration or when opening a connection, the Inverno Http client supports HTTP/1.x, HTTP/2 or H2C (HTTP/2 over cleartext). By default, the client should be configured to connect with HTTP/2 whenever possible and fallback to HTTP/1.1 if the server does not support it. 

> Note that the `open` command actually creates an endpoint and not the actual connection whose lifecycle is managed by the endpoint.

For instance, if you try to connect to the [Inverno Web server example][inverno-example-web] application which supports H2C, the first HTTP request should contain H2C upgrade headers. If the upgrade succeeds, subsequent request will be directly sent using HTTP/2.0:

```
> open localhost 8080
Connected to localhost/<unresolved>:8080
localhost:8080> get /
2023-12-21 09:55:03,992 INFO  [inverno-io-epoll-1-6] i.i.m.h.c.i.AbstractEndpoint - HTTP/1.1 Client (epoll) connected to http://localhost:8080
> GET / h2
> user-agent: Inverno/1.6.0-SNAPSHOT
> upgrade: h2c
> http2-settings: AAEAABAAAAIAAAABAAN_____AAQAAP__AAUAAEAAAAZ_____
> connection: upgrade,http2-settings
> host: localhost:8080

────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
< h2 404 Not Found
< :status: 404
< content-length: 0

localhost:8080> get /
> GET / h2
> :method: GET
> :scheme: http
> :authority: localhost:8080
> :path: /
> user-agent: Inverno/1.6.0-SNAPSHOT

────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
< h2 404 Not Found
< :status: 404
< content-length: 0

localhost:8080> 
```

> Note that for H2C to work, the client must be configured with both HTTP/1.1 and HTTP/2.0 because HTTP/1.1 is needed for the protocol upgrade. Specifying only HTTP/2.0 will result in a direct HTTP/2.0 connection with no upgrade, this will most certainly work when the connection is secured (TLS being used to negotiate the Http protocol version) but it will most probably fail with a clear connection.

Using a secured connection, Http protocol version is negotiated (ALPN) and if the server supports it HTTP/2.0 shall be used:

```
> open -s example.org
Connected to example.org/<unresolved>:443
example.org:443> get /
2023-12-21 10:02:02,908 INFO  [inverno-io-epoll-1-9] i.i.m.h.c.i.AbstractEndpoint - HTTP/2.0 Client (epoll) connected to https://example.org:443
* Server certificate:
   subject: CN=www.example.org, O=Internet Corporation for Assigned Names and Numbers, L=Los Angeles, ST=California, C=US
   start date: Tue Jan 30 01:00:00 CET 2024
   expire date: Sun Mar 02 00:59:59 CET 2025
   subjectAltName: [2, www.example.org], [2, example.net], [2, example.edu], [2, example.com], [2, example.org], [2, www.example.com], [2, www.example.edu], [2, www.example.net]
   issuer: CN=DigiCert Global G2 TLS RSA SHA256 2020 CA1, O=DigiCert Inc, C=US

> GET / h2
> :method: GET
> :scheme: https
> :authority: example.org
> :path: /
> user-agent: Inverno/1.6.0-SNAPSHOT

────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
< h2 200 OK
< :status: 200
< age: 590700
< cache-control: max-age=604800
< content-type: text/html; charset=UTF-8
< date: Thu, 21 Dec 2023 09:02:02 GMT
< etag: "3147526947+gzip+ident"
< expires: Thu, 28 Dec 2023 09:02:02 GMT
< last-modified: Thu, 17 Oct 2019 07:18:26 GMT
< server: ECS (dce/26A0)
< vary: Accept-Encoding
< x-cache: HIT
< content-length: 1256

...
```

> Some web sites (e.g. google.com) requires the client to send [Server Name Indication][sni] to identify the site the client is trying to connect to over TLS. This is disabled by default and can be enabled in the configuration
> 
> ```plaintext
> > config set tls_send_sni true
> ```

The HTTP client can also be configured with a keystore and a truststore to enable Mutual TLS authentication ([mTLS][mTLS]), the example application provides client keystore and trustore under `src/main/resources` matching [HTTP server example][inverno-example-http] certificate, they can be specified on the command line:

```plaintext
$ mvn clean inverno:run -Dinverno.run.arguments='--io.inverno.example.app_http_client.app_http_clientConfiguration.http_client.tls_key_store=\"module:/clientkeystore.p12\" --io.inverno.example.app_http_client.app_http_clientConfiguration.http_client.tls_key_store_type=\"PKCS12\" --io.inverno.example.app_http_client.app_http_clientConfiguration.http_client.tls_key_store_password=\"password\" --io.inverno.example.app_http_client.app_http_clientConfiguration.http_client.tls_trust_store=\"module:/clienttruststore.p12\" --io.inverno.example.app_http_client.app_http_clientConfiguration.http_client.tls_trust_store_type=\"PKCS12\" --io.inverno.example.app_http_client.app_http_clientConfiguration.http_client.tls_trust_store_password=\"password\"'
...
```

When connecting to [HTTP server example][inverno-example-http], the client should now trust the server certificate and send authentication to the server:

```plaintext
> open -s 127.0.0.1 8443
Connected to 127.0.0.1/<unresolved>:8443
127.0.0.1:8443> get /
2024-02-14 16:03:26,541 INFO  [inverno-io-epoll-1-4] i.i.m.h.c.i.AbstractEndpoint - HTTP/2.0 Client (epoll) connected to https://127.0.0.1:8443
* Server certificate:
   subject: CN=Http Example Server, OU=Unknown, O=Inverno, L=Unknown, ST=Unknown, C=Unknown
   start date: Wed Feb 14 12:12:19 CET 2024
   expire date: Fri Dec 23 12:12:19 CET 2033
   subjectAltName: [7, 127.0.0.1], [2, localhost]
   issuer: CN=Http Example Server, OU=Unknown, O=Inverno, L=Unknown, ST=Unknown, C=Unknown

> GET / h2
> :method: GET
> :scheme: https
> :authority: 127.0.0.1:8443
> :path: /
> user-agent: Inverno/1.7.0-SNAPSHOT

────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
< h2 200 OK
< :status: 200
< content-length: 26

Hello Http Example Client!
127.0.0.1:8443> 
```

In above example, `Hello Example Client` is the common name (CN) extracted from the client certificate sent to the server.

## Packaging the application

The application can be packaged as a native runtime image by invoking the `release` build profile:

```plaintext
$ mvn install -Prelease
...
[INFO] --- inverno:${VERSION_INVERNO_TOOLS}:package-app (inverno-package-app) @ inverno-example-http-client ---
 [═══════════════════════════════════════════════ 100 % ══════════════════════════════════════════════] 
[INFO] 
[INFO] --- maven-install-plugin:2.5.2:install (default-install) @ inverno-example-http-client ---
[INFO] Installing /home/jkuhn/Devel/git/frmk/inverno/inverno-examples/inverno-example-http-client/target/inverno-example-http-client-1.0.0-SNAPSHOT.jar to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-http-client/1.0.0-SNAPSHOT/inverno-example-http-client-1.0.0-SNAPSHOT.jar
[INFO] Installing /home/jkuhn/Devel/git/frmk/inverno/inverno-examples/inverno-example-http-client/pom.xml to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-http-client/1.0.0-SNAPSHOT/inverno-example-http-client-1.0.0-SNAPSHOT.pom
[INFO] Installing /home/jkuhn/Devel/git/frmk/inverno/inverno-examples/inverno-example-http-client/target/inverno-example-http-client-1.0.0-SNAPSHOT-application_linux_amd64.zip to /home/jkuhn/.m2/repository/io/inverno/example/inverno-example-http-client/1.0.0-SNAPSHOT/inverno-example-http-client-1.0.0-SNAPSHOT-application_linux_amd64.zip
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

The previous command creates folder `target/inverno-example-http-client-1.0.0-SNAPSHOT-application_linux_amd64` containing the Java runtime and the application and installed the corresponding archive to the Maven repository:

```plaintext
$ ./target/inverno-example-http-client-1.0.0-SNAPSHOT-application_linux_amd64/bin/example-http-client
...
```

## Going further

- [HTTP client module documentation][inverno-mod-http-client]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]


