[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-dist-root]: https://github.com/inverno-io/inverno-dist
[inverno-tool-maven-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-maven-plugin
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[inverno-mod-redis]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-redis/

[epoll]: https://en.wikipedia.org/wiki/Epoll
[lettuce]: https://lettuce.io
[redis]: https://redis.io

# Inverno Redis client example

A sample application showing how to use the Redis client API and the Redis [Lettuce][lettuce] module to query a [Redis][redis] datastore. It basically creates a couple of entries, gets them and finally deletes them from the datastore.

The Redis lettuce client configuration is exposed in the module's configuration `AppConfiguration`.

The client is also configured to use [epoll][epoll] when available (i.e. on Linux platform) for better performance.

## Running the application

The application requires a local Redis server listening on port `6379`, it can be started using Docker as follows:

```plaintext
$ docker run -d -p6379:6379 redis
```

The application is started using the Inverno Maven plugin as follows:

```plaintext
$ mvn inverno:run
...
2024-04-09 15:04:24,099 INFO  [main] i.i.c.v.Application - Inverno is starting...


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
     ║ Java version        : 21.0.2+13-58                                                         ║
     ║ Java home           : /home/jkuhn/Devel/jdk/jdk-21.0.2                                     ║
     ║                                                                                            ║
     ║ Application module  : io.inverno.example.app_redis                                         ║
     ║ Application version : 1.0.0-SNAPSHOT                                                       ║
     ║ Application class   : io.inverno.example.app_redis.Main                                    ║
     ║                                                                                            ║
     ║ Modules             :                                                                      ║
     ║  ...                                                                                       ║
     ╚════════════════════════════════════════════════════════════════════════════════════════════╝


2024-04-09 15:04:24,110 INFO  [main] i.i.e.a.App_redis - Starting Module io.inverno.example.app_redis...
2024-04-09 15:04:24,111 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2024-04-09 15:04:24,442 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 331ms
2024-04-09 15:04:24,443 INFO  [main] i.i.m.r.l.Lettuce - Starting Module io.inverno.mod.redis.lettuce...
2024-04-09 15:04:24,530 INFO  [main] i.i.m.r.l.Lettuce - Module io.inverno.mod.redis.lettuce started in 86ms
2024-04-09 15:04:24,530 INFO  [main] i.i.e.a.App_redis - Module io.inverno.example.app_redis started in 427ms
2024-04-09 15:04:24,530 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_redis started in 465ms
2024-04-09 15:04:24,949 INFO  [inverno-io-epoll-1-1] i.i.e.a.Main - Store Person{id=2, firstname=Linda, name=Johnson, age=25}
2024-04-09 15:04:24,950 INFO  [inverno-io-epoll-1-1] i.i.e.a.Main - Store Person{id=1, firstname=John, name=Smith, age=42}
2024-04-09 15:04:24,994 INFO  [inverno-io-epoll-1-1] i.i.e.a.Main - Get Person{id=2, firstname=Linda, name=Johnson, age=25}
2024-04-09 15:04:24,995 INFO  [inverno-io-epoll-1-1] i.i.e.a.Main - Get Person{id=1, firstname=John, name=Smith, age=42}
2024-04-09 15:04:24,998 INFO  [inverno-io-epoll-1-1] i.i.e.a.Main - Delete true
2024-04-09 15:04:24,998 INFO  [inverno-io-epoll-1-1] i.i.e.a.Main - Delete true
2024-04-09 15:04:24,998 INFO  [main] i.i.e.a.App_redis - Stopping Module io.inverno.example.app_redis...
2024-04-09 15:04:25,010 INFO  [main] i.i.m.b.Boot - Stopping Module io.inverno.mod.boot...
2024-04-09 15:04:25,011 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot stopped in 1ms
2024-04-09 15:04:25,012 INFO  [main] i.i.m.r.l.Lettuce - Stopping Module io.inverno.mod.redis.lettuce...
2024-04-09 15:04:25,012 INFO  [main] i.i.m.r.l.Lettuce - Module io.inverno.mod.redis.lettuce stopped in 0ms
2024-04-09 15:04:25,012 INFO  [main] i.i.e.a.App_redis - Module io.inverno.example.app_redis stopped in 14ms
```

## Going further

- [Redis Client API module][inverno-mod-redis]
- [Inverno distribution documentation][inverno-dist-root]
- [Inverno Maven plugin documentation][inverno-tool-maven-plugin]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]
