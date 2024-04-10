[inverno-mod-irt]: https://github.com/inverno-io/inverno-mods/blob/master/inverno-irt/
[inverno-core-root-doc]: https://github.com/inverno-io/inverno-core/blob/master/doc/reference-guide.md
[inverno-javadoc]: https://inverno.io/docs/release/api/index.html

# Inverno Reactive Template example

A sample Inverno application showing several Inverno Reactive template.

It defines three template sets:

- `Simple.irt` which provides templates to render simple `Message` objects.
- `Items.irt` which provides templates to render `Item` objects in a reactive way and demonstrating template set composition.
- `Stocks.irt` which provides templates to render `Stock` objects.
- `Events.irt` which provides templates to render `Event` objects.

## Running the application

The application is started using the Inverno Maven plugin as follows:

```plaintext
$ mvn inverno:run
09:25:32.967 [main] INFO  io.inverno.example.app_irt.Main - Render simple...
09:25:32.974 [main] INFO  io.inverno.example.app_irt.Main - Render items...
09:25:33.057 [main] INFO  io.inverno.example.app_irt.Main - Render stocks...
09:25:33.068 [main] INFO  io.inverno.example.app_irt.Main - Render events...
```

Templates are rendered in files in `target/` directory: `simple.txt`, `items.html`, `stocks.html`, `events.html`.

## Going further

- [Reactive template module documentation][Inverno-mod-irt]
- [Inverno core documentation][inverno-core-root-doc]
- [API documentation][inverno-javadoc]
