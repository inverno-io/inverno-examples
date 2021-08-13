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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.inverno.example.app_irt.model.Item;
import io.inverno.example.app_irt.model.Message;
import io.inverno.example.app_irt.model.Stock;
import io.inverno.example.app_irt.templates.Items;
import io.inverno.example.app_irt.templates.Simple;
import io.inverno.example.app_irt.templates.Stocks;
import io.inverno.example.app_irt.templates.Stocks.Renderer;
import reactor.core.publisher.Flux;

/**
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
public class Main {
	
	public static void main(String[] args) throws Exception {
		renderSimple();
		renderItems();
		renderStocks();
	}
	
	public static void renderSimple() throws InterruptedException, ExecutionException {
		System.out.println(Simple.string().render(new Message("Hello, world!", false)).get());
	}
	
	public static void renderItems() throws InterruptedException, ExecutionException {
		Flux<Item> items = Flux.just(
			new Item("Item 2", Flux.just("a", "b", "c"), ZonedDateTime.now(), "error"),
			new Item("Item 3", Flux.just("d", "e"), ZonedDateTime.now(), "warning"),
			new Item("Item 1", Flux.just("f"), ZonedDateTime.now(), "info")
		);
		System.out.println(Items.string().render(items).get());
	}
	
	public static void renderStocks() throws InterruptedException, ExecutionException {
		List<Stock> items = Stock.dummyItems();
		Renderer<CompletableFuture<String>> stocksRenderer = Stocks.string();
		
		System.out.println(stocksRenderer.render(items).get());
	}
}
