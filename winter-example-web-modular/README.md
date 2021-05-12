[winter-mod-http-server]: https://github.com/winterframework-io/winter-mods/blob/master/doc/reference-guide.md#http-server
[winter-mod-web]: https://github.com/winterframework-io/winter-mods/blob/master/doc/reference-guide.md#web
[winter-dist-root]: https://github.com/winterframework-io/winter-dist
[winter-root-doc]: https://github.com/winterframework-io/winter/blob/master/doc/reference-guide.md
[winter-tool-maven-plugin]: https://github.com/winterframework-io/winter-tools/blob/master/winter-maven-plugin
[winter-javadoc]: https://winterframework.io/docs/release/api/index.html

# Winter Web modular example

A sample Winter application that shows how to create a modular Web application composed of three modules:

- *admin* module which provides some admin REST resources
- *book* module which provides the book REST resource
- *app* module which composes previous modules with the Winter *web* module

It is not fundamentally different than the [Web application example](../winter-example-web/) but it shows how Web controllers and Web router configurer are aggregated into one single Web router configurer wired to the Web server to configure the exposed routes.

## Building the example

The project can be built with the following command:

```plaintext
$ mvn clean install
```

## Running the example

Previous command should have installed a native application packaged to the local Maven repository, we can run the application by retrieving and extracting this package to a location of our choice:

```plaintext
$ mvn dependency:unpack -Dartifact=io.winterframework.example.winter-example-web-modular:app:1.0.0-SNAPSHOT:zip:application_linux_amd64 -DoutputDirectory=./
```
```plaintext
$ ./winter-example-web-modular-1.0.0-SNAPSHOT/bin/app
```

If we open the SwaggerUI at `http://localhost:8080/open-api`, we should see that we have two OpenAPI specifications corresponding to modules *admin* and *book*:

<img src="src/img/swaggerUI.png" style="display: block; margin: 2em auto;"/>

## Going further

- [HTTP server module documentation][winter-mod-http-server]
- [Web server module documentation][winter-mod-web]
- [Winter distribution documentation][winter-dist-root]
- [Winter Maven plugin documentation][winter-tool-maven-plugin]
- [Winter core documentation][winter-root-doc]
- [API documentation][winter-javadoc]