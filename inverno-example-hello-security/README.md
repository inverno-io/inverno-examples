[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-dist-root]: https://github.com/inverno-io/inverno-dist
[inverno-tool-maven-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-maven-plugin
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[inverno-mod-security]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-security/

[graalvm]: https://www.graalvm.org/
[logback]: https://logback.qos.ch/

# Inverno hello security example

A sample inverno application showing how to use the Inverno security module to secure a simple standalone application.

The application is a simple application with one `HelloService` bean and which requires an authentication to run. The `HelloService` bean exposes a simple `sayHello()` operation which display a greeting message to a strongly identified user. The greeting message also depends on the privileges (i.e. its roles) granted to the authenticated user.

It defines the following users: a *vip* user: `jsmith` with password `password` and a normal user `adoe` with password `password`.

## Running the application

User `jsmith` is a *vip* user, a specific geeting message is then displayed:

```plaintext
$ mvn inverno:run -Dinverno.run.arguments="jsmith password"
Hello my dear friend John!
```

User `adoe` is a normal user, a normal greeting message is then displayed:

```plaintext
$ mvn inverno:run -Dinverno.run.arguments="adoe password"
Hello Alice!
```

The application displays an error message on invalid credentials:

```plaintext
$ mvn inverno:run -Dinverno.run.arguments="jsmith invalid"
io.inverno.mod.security.authentication.InvalidCredentialsException: Invalid credentials
```

## Building a native image

Using [GraalVM][graalvm], you can also build a native image of the application with the following command:

```plaintext
$ mvn clean package -Pnative
```

You can then run the native application:

```plaintext
$ ./target/example-hello-security jsmith password
Hello my dear friend John!
```

> Note that for the native image to work, [logback][logback] must be used as logging manager since log4j doesn't support native build (see https://issues.apache.org/jira/browse/LOG4J2-2649).

## Going further

- [Inverno security module documentation][inverno-mod-security]
- [Inverno distribution documentation][inverno-dist-root]
- [Inverno Maven plugin documentation][inverno-tool-maven-plugin]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]
