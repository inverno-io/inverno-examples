[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-dist-root]: https://github.com/inverno-io/inverno-dist
[inverno-tool-maven-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-maven-plugin
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[inverno-mod-configuration]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-configuration/

# Inverno configuration example

A sample Inverno application showing how to use the configuration module to bootstrap an application with different configuration depending on a context.

The application configuration is defined in `AppConfiguration` interface, a `BootstrapConfigurationSource` is created when the application starts to resolve the parameters defining the context: the **node** and the **environment**, these are then injected into the application to load the actual configuration.

## Running the application

The application can be run with the default configuration using the Inverno Maven plugin as follows:

```plaintext
mvn inverno:run
...
2021-04-26 09:09:02,400 INFO  [main] i.w.e.a.Main - App Configuration [  ]:
2021-04-26 09:09:02,400 INFO  [main] i.w.e.a.Main -  * message: Default message
2021-04-26 09:09:02,401 INFO  [main] i.w.e.a.Main -  * id: 0
2021-04-26 09:09:02,401 INFO  [main] i.w.e.a.Main -  * uri: null
2021-04-26 09:09:02,401 INFO  [main] i.w.e.a.Main -  * uris: https://server1.example.com, https://server2.example.com
2021-04-26 09:09:02,401 INFO  [main] i.w.e.a.Main -  * date: null
2021-04-26 09:09:02,401 INFO  [main] i.w.e.a.Main -  * sub_configuration.param: null
...
```

Individual parameters can be set as command line arguments or system properties as follows:

```plaintext
$ mvn inverno:run -Dinverno.run.arguments='--io.inverno.example.app_config.appConfiguration.date=\"2021-01-01\"' -Dinverno.exec.vmOptions='-Dio.inverno.example.app_config.appConfiguration.id=123'
...
2021-04-26 09:13:20,456 INFO  [main] i.w.e.a.Main - App Configuration [  ]:
2021-04-26 09:13:20,456 INFO  [main] i.w.e.a.Main -  * message: Default message
2021-04-26 09:13:20,456 INFO  [main] i.w.e.a.Main -  * id: 123
2021-04-26 09:13:20,456 INFO  [main] i.w.e.a.Main -  * uri: null
2021-04-26 09:13:20,456 INFO  [main] i.w.e.a.Main -  * uris: https://server1.example.com, https://server2.example.com
2021-04-26 09:13:20,457 INFO  [main] i.w.e.a.Main -  * date: 2021-01-01
2021-04-26 09:13:20,457 INFO  [main] i.w.e.a.Main -  * sub_configuration.param: null
...
```

> On Windows systems, three double quotes must be used to obtain a single double quote, each of which must be escaped within the quoted system property value: `-Dinverno.run.arguments='--io.inverno.example.app_config.appConfiguration.date=\"\"\"2021-01-01\"\"\"'` 

Parameterized configurations for a particular context are defined in `src/main/resources/configuration.cprops`:

```plaintext
io.inverno.example.app_config.appConfiguration {
    [ environment = "test" ] {
        message = "Test message"
        integer = 123
        uri = "http://test"
    }
    
    [ environment = "production" ] {
        message = "Production message"
        date = "2021-01-01"
        [ node = "node-1" ] {
            integer = 1
            uri = "https://node-1.production"
            sub_configuration {
                param = "Parameter for node-1 in production"
            }
        }
        [ node = "node-2" ] {
            integer = 2
            uri = "https://node-2.production"
            sub_configuration {
                param = "Parameter for node-2 in production"
            }
        }
    }
}
```

We can start the application with the `test` configuration by specifying the `environment` property:

```plaintext
$ mvn inverno:run -Dinverno.exec.vmOptions="-Denvironment=test"
...
2021-04-26 09:22:14,840 INFO  [main] i.w.e.a.Main - App Configuration [ environment=test ]:
2021-04-26 09:22:14,840 INFO  [main] i.w.e.a.Main -  * message: Test message
2021-04-26 09:22:14,840 INFO  [main] i.w.e.a.Main -  * id: 123
2021-04-26 09:22:14,840 INFO  [main] i.w.e.a.Main -  * uri: http://test
2021-04-26 09:22:14,840 INFO  [main] i.w.e.a.Main -  * uris: https://server1.example.com, https://server2.example.com
2021-04-26 09:22:14,841 INFO  [main] i.w.e.a.Main -  * date: null
2021-04-26 09:22:14,841 INFO  [main] i.w.e.a.Main -  * sub_configuration.param: Parameter in test
...
```

Or start the application with the `production` configuration on node `node-1`:

```plaintext
$ mvn inverno:run -Dinverno.exec.vmOptions="-Denvironment=production -Dnode=node-1"
...
2021-04-26 09:20:14,355 INFO  [main] i.w.e.a.Main - App Configuration [ node=node-1, environment=production ]:
2021-04-26 09:20:14,356 INFO  [main] i.w.e.a.Main -  * message: Production message
2021-04-26 09:20:14,356 INFO  [main] i.w.e.a.Main -  * id: 1
2021-04-26 09:20:14,356 INFO  [main] i.w.e.a.Main -  * uri: https://node-1.production
2021-04-26 09:20:14,356 INFO  [main] i.w.e.a.Main -  * uris: https://server1.example.com, https://server2.example.com
2021-04-26 09:20:14,357 INFO  [main] i.w.e.a.Main -  * date: 2021-01-01
2021-04-26 09:20:14,357 INFO  [main] i.w.e.a.Main -  * sub_configuration.param: Parameter for node-1 in production
...
```

Or start the application with the `production` configuration on node `node-2`:

```plaintext
$ mvn inverno:run -Dinverno.exec.vmOptions="-Denvironment=production -Dnode=node-2"
...
2021-04-26 09:20:33,215 INFO  [main] i.w.e.a.Main - App Configuration [ node=node-2, environment=production ]:
2021-04-26 09:20:33,215 INFO  [main] i.w.e.a.Main -  * message: Production message
2021-04-26 09:20:33,216 INFO  [main] i.w.e.a.Main -  * id: 2
2021-04-26 09:20:33,216 INFO  [main] i.w.e.a.Main -  * uri: https://node-2.production
2021-04-26 09:20:33,216 INFO  [main] i.w.e.a.Main -  * uris: https://server1.example.com, https://server2.example.com
2021-04-26 09:20:33,216 INFO  [main] i.w.e.a.Main -  * date: 2021-01-01
2021-04-26 09:20:33,216 INFO  [main] i.w.e.a.Main -  * sub_configuration.param: Parameter for node-2 in production
...
```

If we specify another unconfigured node, it defaults to the `production` configuration:

```plaintext
$ mvn inverno:run -Dinverno.exec.vmOptions="-Denvironment=production -Dnode=node-3"
...
2021-04-26 09:20:55,826 INFO  [main] i.w.e.a.Main -  * message: Production message
2021-04-26 09:20:55,826 INFO  [main] i.w.e.a.Main -  * id: 0
2021-04-26 09:20:55,826 INFO  [main] i.w.e.a.Main -  * uri: null
2021-04-26 09:20:55,826 INFO  [main] i.w.e.a.Main -  * uris: https://server1.example.com, https://server2.example.com
2021-04-26 09:20:55,827 INFO  [main] i.w.e.a.Main -  * date: 2021-01-01
2021-04-26 09:20:55,827 INFO  [main] i.w.e.a.Main -  * sub_configuration.param: null
...
```

And if we only specify the node, it defaults to the default configuration since we haven't specified any configuration for nodes outside the `production` context:

```plaintext
$ mvn inverno:run -Dinverno.exec.vmOptions="-Dnode=node-1"
...
2021-04-26 09:23:51,816 INFO  [main] i.w.e.a.Main - App Configuration [ node=node-1 ]:
2021-04-26 09:23:51,816 INFO  [main] i.w.e.a.Main -  * message: Default message
2021-04-26 09:23:51,817 INFO  [main] i.w.e.a.Main -  * id: 0
2021-04-26 09:23:51,817 INFO  [main] i.w.e.a.Main -  * uri: null
2021-04-26 09:23:51,817 INFO  [main] i.w.e.a.Main -  * uris: https://server1.example.com, https://server2.example.com
2021-04-26 09:23:51,817 INFO  [main] i.w.e.a.Main -  * date: null
2021-04-26 09:23:51,817 INFO  [main] i.w.e.a.Main -  * sub_configuration.param: null
...
```

## Going further

- [Configuration module documentation][Inverno-mod-configuration]
- [Inverno distribution documentation][inverno-dist-root]
- [Inverno Maven plugin documentation][inverno-tool-maven-plugin]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]
