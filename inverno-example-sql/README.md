[inverno-mod-sql]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-sql/
[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

[epoll]: https://en.wikipedia.org/wiki/Epoll
[vertx]: https://vertx.io
[postgresql]: https://www.postgresql.org/

# Inverno SQL client example

A sample application showing how to use the SQL client API and the SQL [Vert.x][vertx] module to query a [PostgreSQL][postgresql] RDBMS. It basically creates a couple of entries in a table, gets them and finally deletes them from the datastore.

The SQL Vert.x client configuration is exposed in the module's configuration `App_sqlConfiguration`.

The client is also configured to use [epoll][epoll] when available (ie. on Linux platform) for better performance.

## Running the example

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

The application can then be run as follows:

``plaintext
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
     ║ Application module  : io.inverno.example.app_sql                                           ║
     ║ Application class   : io.inverno.example.app_sql.Main                                      ║
     ║                                                                                            ║
     ║ Modules             :                                                                      ║
     ║  * ...                                                                                     ║
     ╚════════════════════════════════════════════════════════════════════════════════════════════╝


2022-01-14 15:23:26,761 INFO  [main] i.i.e.a.App_sql - Starting Module io.inverno.example.app_sql...
2022-01-14 15:23:26,761 INFO  [main] i.i.m.b.Boot - Starting Module io.inverno.mod.boot...
2022-01-14 15:23:27,077 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot started in 315ms
2022-01-14 15:23:27,077 INFO  [main] i.i.m.s.v.Vertx - Starting Module io.inverno.mod.sql.vertx...
2022-01-14 15:23:27,165 INFO  [main] i.i.m.s.v.Vertx - Module io.inverno.mod.sql.vertx started in 87ms
2022-01-14 15:23:27,165 INFO  [main] i.i.e.a.App_sql - Module io.inverno.example.app_sql started in 406ms
2022-01-14 15:23:27,168 INFO  [main] i.i.c.v.Application - Application io.inverno.example.app_sql started in 441ms
2022-01-14 15:23:27,352 INFO  [vert.x-eventloop-thread-0] i.i.e.a.Main - Store Person{id=5, firstname=John, lastname=Smith, age=42}
2022-01-14 15:23:27,353 INFO  [vert.x-eventloop-thread-0] i.i.e.a.Main - Store Person{id=6, firstname=Linda, lastname=Johnson, age=25}
2022-01-14 15:23:27,358 INFO  [vert.x-eventloop-thread-0] i.i.e.a.Main - Get Person{id=5, firstname=John, lastname=Smith, age=42}
2022-01-14 15:23:27,358 INFO  [vert.x-eventloop-thread-0] i.i.e.a.Main - Get Person{id=6, firstname=Linda, lastname=Johnson, age=25}
2022-01-14 15:23:27,368 INFO  [vert.x-eventloop-thread-0] i.i.e.a.Main - Delete true
2022-01-14 15:23:27,371 INFO  [vert.x-eventloop-thread-0] i.i.e.a.Main - Delete true
2022-01-14 15:23:27,372 INFO  [main] i.i.e.a.App_sql - Stopping Module io.inverno.example.app_sql...
2022-01-14 15:23:27,385 INFO  [main] i.i.m.b.Boot - Stopping Module io.inverno.mod.boot...
2022-01-14 15:23:27,386 INFO  [main] i.i.m.b.Boot - Module io.inverno.mod.boot stopped in 1ms
2022-01-14 15:23:27,386 INFO  [main] i.i.m.s.v.Vertx - Stopping Module io.inverno.mod.sql.vertx...
2022-01-14 15:23:27,387 INFO  [main] i.i.m.s.v.Vertx - Module io.inverno.mod.sql.vertx stopped in 0ms
2022-01-14 15:23:27,387 INFO  [main] i.i.e.a.App_sql - Module io.inverno.example.app_sql stopped in 15ms
2022-01-14 15:23:28,049 INFO  [Thread-0] i.i.e.a.App_sql - Stopping Module io.inverno.example.app_sql...
2022-01-14 15:23:28,050 INFO  [Thread-0] i.i.m.b.Boot - Stopping Module io.inverno.mod.boot...
2022-01-14 15:23:28,050 INFO  [Thread-0] i.i.m.b.Boot - Module io.inverno.mod.boot stopped in 0ms
2022-01-14 15:23:28,050 INFO  [Thread-0] i.i.m.s.v.Vertx - Stopping Module io.inverno.mod.sql.vertx...
2022-01-14 15:23:28,051 INFO  [Thread-0] i.i.m.s.v.Vertx - Module io.inverno.mod.sql.vertx stopped in 0ms
2022-01-14 15:23:28,051 INFO  [Thread-0] i.i.e.a.App_sql - Module io.inverno.example.app_sql stopped in 1ms
```

## Going further

- [SQL Client API module][inverno-mod-sql]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]
