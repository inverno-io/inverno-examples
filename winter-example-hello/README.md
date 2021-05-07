[winter-root-doc]: https://github.com/winterframework-io/winter/blob/master/doc/reference-guide.md
[javadoc]: http://tbd

# Winter hello example

A sample Winter application showing basic IoC/DI with Winter core framework.

The application simply says hello to a user specified as command line arguments with a greeting message. The `HelloService` is used to display the message, when starting the module, the framework creates an instance (IoC) and wire the greeting message specified in optional socket bean `GreetingMessageSocketBean` (DI) into the bean's optional socket `setGreetingMessage()`.

## Running the example

```plaintext
$ mvn winter:run -Dwinter.run.arguments="John"
Hello John, how are you today?
```

## Going further

- [Winter core documentation][winter-root-doc]
- [API documentation][javadoc]