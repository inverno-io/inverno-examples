[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-mod-security]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-security/
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[graalvm]: https://www.graalvm.org/

# Inverno hello security example

A sample inverno application showing how to use the Inverno security module to secure a simple standalone application.

The application is a simple application with one `HelloService` bean and which requires an authentication to run. The `HelloService` bean exposes a simple `sayHello()` operation which display a greeting message to an strongly identified user. The greeting message also depends on the priviledges (i.e. its roles) granted to the authenticated user.

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

## Going further

- [Inverno security module documentation][inverno-mod-security]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]
