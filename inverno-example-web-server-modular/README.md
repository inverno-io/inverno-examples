[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-dist-root]: https://github.com/inverno-io/inverno-dist
[inverno-tool-maven-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-maven-plugin
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[inverno-mod-http-server]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-http-server/
[inverno-mod-web-server]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-web-server/

[inverno-examples-web-server]: ../inverno-example-web-server

# Inverno Web modular example

A sample Inverno application that shows how to create a modular Web application composed of three modules:

- *admin* module which provides some admin REST resources
- *book* module which provides the book REST resource
- *app* module which composes previous modules with the Inverno *web* module

It is not fundamentally different from the [Web application example][inverno-examples-web-server], but it shows how multiple Web modules providing Web controllers and Web server configurers can be composed in a single Web server application module.

## Building and packaging the application

The project can be built and packaged as a native runtime image with the following command:

```plaintext
$ mvn clean install -Prelease
```

## Running the application

Previous command should have installed a native application packaged to the local Maven repository, we can run the application by retrieving and extracting this package to a location of our choice:

```plaintext
$ mvn dependency:unpack -Dartifact=io.inverno.example.inverno-example-web-server-modular:app:1.0.0-SNAPSHOT:zip:application_linux_amd64 -DoutputDirectory=./
```
```plaintext
$ ./inverno-example-web-modular-1.0.0-SNAPSHOT/bin/example-web-server-modular
```

If we open the SwaggerUI at `http://localhost:8080/open-api`, we should see that we have two OpenAPI specifications corresponding to modules *admin* and *book*:

<img src="src/img/swaggerUI.png" alt="Inverno SwaggerUI" style="display: block; margin: 2em auto;"/>

## Going further

- [HTTP server module documentation][inverno-mod-http-server]
- [Web server module documentation][inverno-mod-web-server]
- [Inverno distribution documentation][inverno-dist-root]
- [Inverno Maven plugin documentation][inverno-tool-maven-plugin]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]
