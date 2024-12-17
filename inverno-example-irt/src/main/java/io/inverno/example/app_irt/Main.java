/*
 * Copyright 2021 Jeremy KUHN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.inverno.example.app_irt;

import io.inverno.example.app_irt.model.Event;
import io.inverno.example.app_irt.model.Event.Type;
import io.inverno.example.app_irt.model.Item;
import io.inverno.example.app_irt.model.Message;
import io.inverno.example.app_irt.model.Stock;
import io.inverno.example.app_irt.templates.Events;
import io.inverno.example.app_irt.templates.Items;
import io.inverno.example.app_irt.templates.Simple;
import io.inverno.example.app_irt.templates.Stocks;
import io.inverno.example.app_irt.templates.Stocks.Renderer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;

/**
 * <p>
 * Demonstrates how to use the Inverno Reactive Template module for data rendering.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
public class Main {
	
	private static final Logger LOGGER = LogManager.getLogger(Main.class);
	
	public static void main(String[] args) throws Exception {
		renderSimple();
		renderItems();
		renderStocks();
		renderEvents();
	}
	
	public static void renderSimple() throws InterruptedException, ExecutionException, IOException {
		LOGGER.info("Render simple...");
		Files.writeString(Path.of("target/simple.txt"), Simple.string().render(new Message("Hello, world!", false)).get(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}
	
	public static void renderItems() throws InterruptedException, ExecutionException, IOException {
		LOGGER.info("Render items...");
		Flux<Item> items = Flux.just(
			new Item("Item 2", Flux.just("a", "b", "c"), ZonedDateTime.now(), "error"),
			new Item("Item 3", Flux.just("d", "e"), ZonedDateTime.now(), "warning"),
			new Item("Item 1", Flux.just("f"), ZonedDateTime.now(), "info")
		);
		Files.writeString(Path.of("target/items.html"), Items.string().render(items).get(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}
	
	public static void renderStocks() throws InterruptedException, ExecutionException, IOException {
		LOGGER.info("Render stocks...");
		List<Stock> items = Stock.dummyItems();
		Renderer<CompletableFuture<String>> stocksRenderer = Stocks.string();
		Files.writeString(Path.of("target/stocks.html"), stocksRenderer.render(items).get(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}
	
	public static void renderEvents() throws InterruptedException, ExecutionException, IOException {
		LOGGER.info("Render events...");
		Flux<Event> events = Flux.just(
			new Event(Type.ERROR, ZonedDateTime.now().minusSeconds(5), "This is an error."),
			new Event(Type.WARN, ZonedDateTime.now().minusSeconds(10), "This is a warning."),
			new Event(Type.INFO, ZonedDateTime.now().minusSeconds(15), "This is an info.")
		).repeat(10);
		Files.writeString(Path.of("target/events.html"), Events.string().render(events).get(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}
}
