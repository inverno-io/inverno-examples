[winter-mod-configuration]: https://github.com/winterframework-io/winter-mods/blob/master/doc/reference-guide.md#configuration-1
[winter-root-doc]: https://github.com/winterframework-io/winter/blob/master/doc/reference-guide.md
[winter-javadoc]: https://winterframework.io/docs/release/api/index.html

# Winter configuration example

A sample Winter application showing how to use the configuration module to bootstrap an application with different configuration depending on a context.

The application configuration is defined in `AppConfiguration` interface, a `BootstrapConfigurationSource` is created when the application starts to resolve the parameters defining the context: the **node** and the **environment**, these are then injected into the application to load the actual configuration.

## Running the example

The application can be run with the default configuration using the Winter Maven plugin as follows:

```plaintext
mvn winter:run
...
2021-04-26 09:09:02,400 INFO  [main] i.w.e.a.Main - App Configuration [  ]:
2021-04-26 09:09:02,400 INFO  [main] i.w.e.a.Main -  * message: Default message
2021-04-26 09:09:02,401 INFO  [main] i.w.e.a.Main -  * id: 0
2021-04-26 09:09:02,401 INFO  [main] i.w.e.a.Main -  * uri: null
2021-04-26 09:09:02,401 INFO  [main] i.w.e.a.Main -  * date: null
...
```

Individual parameters can be set as command line arguments or system properties as follows:

```plaintext
$ mvn winter:run -Dwinter.run.arguments="--io.winterframework.example.app_config.appConfiguration.date=\\\"2021-01-01\\\"" -Dwinter.exec.vmOptions="-Dio.winterframework.example.app_config.appConfiguration.integer=123"
...
2021-04-26 09:13:20,456 INFO  [main] i.w.e.a.Main - App Configuration [  ]:
2021-04-26 09:13:20,456 INFO  [main] i.w.e.a.Main -  * message: Default message
2021-04-26 09:13:20,456 INFO  [main] i.w.e.a.Main -  * id: 123
2021-04-26 09:13:20,456 INFO  [main] i.w.e.a.Main -  * uri: null
2021-04-26 09:13:20,457 INFO  [main] i.w.e.a.Main -  * date: 2021-01-01
...
```

Parameterized configurations for a particular context are defined in `src/main/resources/configuration.cprops`:

```plaintext
io.winterframework.example.app_config.appConfiguration {
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
		}
		[ node = "node-2" ] {
			integer = 2
			uri = "https://node-2.production"
		}
	}
}
```

We can start the application with the `test` configuration by specifying the `environment` property:

```plaintext
$ mvn winter:run -Dwinter.exec.vmOptions="-Denvironment=test"
...
2021-04-26 09:22:14,840 INFO  [main] i.w.e.a.Main - App Configuration [ environment=test ]:
2021-04-26 09:22:14,840 INFO  [main] i.w.e.a.Main -  * message: Test message
2021-04-26 09:22:14,840 INFO  [main] i.w.e.a.Main -  * id: 123
2021-04-26 09:22:14,840 INFO  [main] i.w.e.a.Main -  * uri: http://test
2021-04-26 09:22:14,841 INFO  [main] i.w.e.a.Main -  * date: null
...
```

Or start the application with the `production` configuration on node `node-1`:

```plaintext
$ mvn winter:run -Dwinter.exec.vmOptions="-Dnode=node-1 -Denvironment=production"
...
2021-04-26 09:20:14,355 INFO  [main] i.w.e.a.Main - App Configuration [ node=node-1, environment=production ]:
2021-04-26 09:20:14,356 INFO  [main] i.w.e.a.Main -  * message: Production message
2021-04-26 09:20:14,356 INFO  [main] i.w.e.a.Main -  * id: 1
2021-04-26 09:20:14,356 INFO  [main] i.w.e.a.Main -  * uri: https://node-1.production
2021-04-26 09:20:14,357 INFO  [main] i.w.e.a.Main -  * date: 2021-01-01
...
```

Or start the application with the `production` configuration on node `node-2`:

```plaintext
$ mvn winter:run -Dwinter.exec.vmOptions="-Dnode=node-2 -Denvironment=production"
...
2021-04-26 09:20:33,215 INFO  [main] i.w.e.a.Main - App Configuration [ node=node-2, environment=production ]:
2021-04-26 09:20:33,215 INFO  [main] i.w.e.a.Main -  * message: Production message
2021-04-26 09:20:33,216 INFO  [main] i.w.e.a.Main -  * id: 2
2021-04-26 09:20:33,216 INFO  [main] i.w.e.a.Main -  * uri: https://node-2.production
2021-04-26 09:20:33,216 INFO  [main] i.w.e.a.Main -  * date: 2021-01-01
...
```

If we specify another unconfigured node, it defaults to the `production` configuration:

```plaintext
$ mvn winter:run -Dwinter.exec.vmOptions="-Dnode=node-3 -Denvironment=production"
...
2021-04-26 09:20:55,826 INFO  [main] i.w.e.a.Main -  * message: Production message
2021-04-26 09:20:55,826 INFO  [main] i.w.e.a.Main -  * id: 0
2021-04-26 09:20:55,826 INFO  [main] i.w.e.a.Main -  * uri: null
2021-04-26 09:20:55,827 INFO  [main] i.w.e.a.Main -  * date: 2021-01-01
...
```

And if we only specify the node, it defaults to the default configuration since we haven't specified any configuration for nodes outside the `production` context:

```plaintext
$ mvn winter:run -Dwinter.exec.vmOptions="-Dnode=node-1"
...
2021-04-26 09:23:51,816 INFO  [main] i.w.e.a.Main - App Configuration [ node=node-1 ]:
2021-04-26 09:23:51,816 INFO  [main] i.w.e.a.Main -  * message: Default message
2021-04-26 09:23:51,817 INFO  [main] i.w.e.a.Main -  * id: 0
2021-04-26 09:23:51,817 INFO  [main] i.w.e.a.Main -  * uri: null
2021-04-26 09:23:51,817 INFO  [main] i.w.e.a.Main -  * date: null
...
```

## Going further

- [Configuration module documentation][winter-mod-configuration]
- [Winter core documentation][winter-root-doc]
- [API documentation][winter-javadoc]