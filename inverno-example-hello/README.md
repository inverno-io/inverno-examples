[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[graalvm]: https://www.graalvm.org/
[logback]: https://logback.qos.ch/

# Inverno hello example

A sample inverno application showing basic IoC/DI with Inverno core framework.

The application simply says hello to a user specified as command line arguments with a greeting message. The `HelloService` is used to display the message, when starting the module, the framework creates an instance (IoC) and wire the greeting message specified in optional socket bean `GreetingMessageSocketBean` (DI) into the bean's optional socket `setGreetingMessage()`.

## Running the application

```plaintext
$ mvn inverno:run -Dinverno.run.arguments="John"
Hello John, how are you today?
```

## Building a native image

Using [GraalVM][graalvm], you can also build a native image of the application with the following command:

```plaintext
> mvn clean package -Pnative
```

You can then run the native application:

```plaintext
> ./target/inverno-example-hello John
Hello John, how are you today?
```

> Note that for the native image to work, [logback][logback] must be used as logging manager since log4j doesn't support native build (see https://issues.apache.org/jira/browse/LOG4J2-2649).

## Going further

- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]