[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-dist-root]: https://github.com/inverno-io/inverno-dist
[inverno-tool-maven-plugin]: https://github.com/inverno-io/inverno-tools/blob/master/inverno-maven-plugin
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[inverno-mod-sql]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-sql/

[epoll]: https://en.wikipedia.org/wiki/Epoll
[vertx]: https://vertx.io
[postgresql]: https://www.postgresql.org/

# Inverno SQL client example

A sample application showing how to use the SQL client API and the SQL [Vert.x][vertx] module to query a [PostgreSQL][postgresql] RDBMS. It basically creates a couple of entries in a table, gets them and finally deletes them from the datastore.

The SQL Vert.x client configuration is exposed in the module's configuration `AppConfiguration`.

The client is also configured to use [epoll][epoll] when available (ie. on Linux platform) for better performance.

## Running the application

The application requires a local Postgres server listening on port `5432`, it can be started using Docker as follows:

```plaintext
$ docker run -d --network inverno-network --name inverno-postgres -e POSTGRES_PASSWORD=password -p5432:5432 postgres
```

The following command can also be used to connect a Postgres client to the database in order to setup schemas:

```plaintext
$ docker run -it --rm --network inverno-network postgres psql -h inverno-postgres -U postgres
```

The database schema creation script is located in project's resources (`src/main/resources/schema.sql`):

```sql
CREATE SEQUENCE person_id_seq;

CREATE TABLE person(id INTEGER DEFAULT NEXTVAL('person_id_seq'), firstname VARCHAR(32), lastname VARCHAR(32), age SMALLINT);
```

The application is started using the Inverno Maven plugin as follows:

```plaintext
$ mvn inverno:run
2024-04-09 15:07:01,298 INFO  [main] i.i.c.v.Application - Inverno is starting...


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
     ║ Application module  : io.inverno.example.app_sql                                           ║
     ║ Application version : 1.0.0-SNAPSHOT                                                       ║
     ║ Application class   : io.inverno.example.app_sql.Main                                      ║
     ║                                                                                            ║
     ║ Modules             :                                                                      ║
     ║  ...                                                                                       ║
     ╚════════════════════════════════════════════════════════════════════════════════════════════╝


2024-04-09 15:07:01,309 INFO  [main] i.i.e.a.App_sql - Starting Module io.inverno.example.app_sql...
2024-04-09 15:07:01,310 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2024-04-09 15:07:01,662 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 351ms
2024-04-09 15:07:01,662 INFO  [main] i.i.m.s.v.Vertx - Starting Module io.inverno.mod.sql.vertx...
2024-04-09 15:07:01,755 INFO  [main] i.i.m.s.v.Vertx - Module io.inverno.mod.sql.vertx started in 92ms
2024-04-09 15:07:01,755 INFO  [main] i.i.e.a.App_sql - Module io.inverno.example.app_sql started in 455ms
2024-04-09 15:07:01,756 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_sql started in 489ms
2024-04-09 15:07:02,048 INFO  [vert.x-eventloop-thread-0] i.i.e.a.Main - Store Person{id=15, firstname=John, lastname=Smith, age=42}
2024-04-09 15:07:02,049 INFO  [vert.x-eventloop-thread-0] i.i.e.a.Main - Store Person{id=16, firstname=Linda, lastname=Johnson, age=25}
2024-04-09 15:07:02,054 INFO  [vert.x-eventloop-thread-0] i.i.e.a.Main - Get Person{id=15, firstname=John, lastname=Smith, age=42}
2024-04-09 15:07:02,055 INFO  [vert.x-eventloop-thread-0] i.i.e.a.Main - Get Person{id=16, firstname=Linda, lastname=Johnson, age=25}
2024-04-09 15:07:02,060 INFO  [vert.x-eventloop-thread-0] i.i.e.a.Main - Delete true
2024-04-09 15:07:02,060 INFO  [vert.x-eventloop-thread-0] i.i.e.a.Main - Delete true
2024-04-09 15:07:02,061 INFO  [main] i.i.e.a.App_sql - Stopping Module io.inverno.example.app_sql...
2024-04-09 15:07:02,070 INFO  [main] i.i.m.b.Boot - Stopping Module io.inverno.mod.boot...
2024-04-09 15:07:02,070 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot stopped in 0ms
2024-04-09 15:07:02,070 INFO  [main] i.i.m.s.v.Vertx - Stopping Module io.inverno.mod.sql.vertx...
2024-04-09 15:07:02,071 INFO  [main] i.i.m.s.v.Vertx - Module io.inverno.mod.sql.vertx stopped in 0ms
2024-04-09 15:07:02,071 INFO  [main] i.i.e.a.App_sql - Module io.inverno.example.app_sql stopped in 10ms
```

## Going further

- [SQL Client API module][inverno-mod-sql]
- [Inverno distribution documentation][inverno-dist-root]
- [Inverno Maven plugin documentation][inverno-tool-maven-plugin]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]

