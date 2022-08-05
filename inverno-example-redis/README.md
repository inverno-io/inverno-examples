[inverno-mod-redis]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-redis/
[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[epoll]: https://en.wikipedia.org/wiki/Epoll
[lettuce]: https://lettuce.io
[redis]: https://redis.io

# Inverno Redis client example

A sample application showing how to use the Redis client API and the Redis [Lettuce][lettuce] module to query a [Redis][redis] datastore. It basically creates a couple of entries, gets them and finally deletes them from the datastore.

The Redis lettuce client configuration is exposed in the module's configuration `App_redisConfiguration`.

The client is also configured to use [epoll][epoll] when available (ie. on Linux platform) for better performance.

## Running the example

The application requires a local Redis server listening on port `6379`, it can be started using Docker as follows:

```plaintext
$ docker run -d -p6379:6379 redis
```

The application can then be run as follows:

```plaintext
$ mvn inverno:run
2021-04-26 10:15:53,299 INFO  [main] i.w.c.v.Application - Inverno is starting...


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
     ║ Java version        : 17+35-2724                                                           ║
     ║ Java home           : /home/jkuhn/Devel/jdk/jdk-17                                         ║
     ║                                                                                            ║
     ║ Application module  : io.inverno.example.app_redis                                         ║
     ║ Application class   : io.inverno.example.app_redis.Main                                    ║
     ║                                                                                            ║
     ║ Modules             :                                                                      ║
     ║  * ...                                                                                     ║
     ╚════════════════════════════════════════════════════════════════════════════════════════════╝


2022-01-14 14:31:49,688 INFO  [main] i.i.e.a.App_redis - Starting Module io.inverno.example.app_redis...
2022-01-14 14:31:49,689 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2022-01-14 14:31:49,978 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 289ms
2022-01-14 14:31:49,978 INFO  [main] i.i.m.r.l.Lettuce - Starting Module io.inverno.mod.redis.lettuce...
2022-01-14 14:31:50,025 INFO  [main] i.i.m.r.l.Lettuce - Module io.inverno.mod.redis.lettuce started in 46ms
2022-01-14 14:31:50,026 INFO  [main] i.i.e.a.App_redis - Module io.inverno.example.app_redis started in 339ms
2022-01-14 14:31:50,028 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_redis started in 378ms
2022-01-14 14:31:50,410 INFO  [inverno-io-epoll-3-2] i.i.e.a.Main - Store Person{id=3, firstname=John, name=Smith, age=42}
2022-01-14 14:31:50,410 INFO  [inverno-io-epoll-3-2] i.i.e.a.Main - Store Person{id=4, firstname=Linda, name=Johnson, age=25}
2022-01-14 14:31:50,435 INFO  [inverno-io-epoll-3-2] i.i.e.a.Main - Get Person{id=3, firstname=John, name=Smith, age=42}
2022-01-14 14:31:50,435 INFO  [inverno-io-epoll-3-2] i.i.e.a.Main - Get Person{id=4, firstname=Linda, name=Johnson, age=25}
2022-01-14 14:31:50,438 INFO  [inverno-io-epoll-3-2] i.i.e.a.Main - Delete true
2022-01-14 14:31:50,439 INFO  [inverno-io-epoll-3-1] i.i.e.a.Main - Delete true
2022-01-14 14:31:50,439 INFO  [main] i.i.e.a.App_redis - Stopping Module io.inverno.example.app_redis...
2022-01-14 14:31:50,450 INFO  [main] i.i.m.b.Boot - Stopping Module io.inverno.mod.boot...
2022-01-14 14:31:50,451 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot stopped in 0ms
2022-01-14 14:31:50,451 INFO  [main] i.i.m.r.l.Lettuce - Stopping Module io.inverno.mod.redis.lettuce...
2022-01-14 14:31:50,451 INFO  [main] i.i.m.r.l.Lettuce - Module io.inverno.mod.redis.lettuce stopped in 0ms
2022-01-14 14:31:50,452 INFO  [main] i.i.e.a.App_redis - Module io.inverno.example.app_redis stopped in 12ms
2022-01-14 14:31:50,951 INFO  [Thread-0] i.i.e.a.App_redis - Stopping Module io.inverno.example.app_redis...
2022-01-14 14:31:50,952 INFO  [Thread-0] i.i.m.b.Boot - Stopping Module io.inverno.mod.boot...
2022-01-14 14:31:50,953 INFO  [Thread-0] i.i.m.b.Boot - Module io.inverno.mod.boot stopped in 0ms
2022-01-14 14:31:50,953 INFO  [Thread-0] i.i.m.r.l.Lettuce - Stopping Module io.inverno.mod.redis.lettuce...
2022-01-14 14:31:50,954 INFO  [Thread-0] i.i.m.r.l.Lettuce - Module io.inverno.mod.redis.lettuce stopped in 0ms
2022-01-14 14:31:50,954 INFO  [Thread-0] i.i.e.a.App_redis - Module io.inverno.example.app_redis stopped in 3ms
```

## Going further

- [Redis Client API module][inverno-mod-redis]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]
