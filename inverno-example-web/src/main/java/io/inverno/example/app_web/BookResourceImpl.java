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
package io.inverno.example.app_web;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.reactivestreams.Publisher;

import io.inverno.core.annotation.Bean;
import io.inverno.example.app_web.dto.Book;
import io.inverno.example.app_web.dto.BookEvent;
import io.inverno.example.app_web.dto.Result;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.http.base.BadRequestException;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.NotFoundException;
import io.inverno.mod.http.base.Status;
import io.inverno.mod.http.base.header.Headers;
import io.inverno.mod.web.WebExchange;
import io.inverno.mod.web.WebPart;
import io.inverno.mod.web.WebResponseBody;
import io.inverno.mod.web.annotation.Body;
import io.inverno.mod.web.annotation.SseEventFactory;
import io.inverno.mod.web.annotation.WebRoute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
@Bean
public class BookResourceImpl implements BookResource {

	private Map<String, Book> bookDB = new ConcurrentHashMap<>(); 
	
	@Override
	public Mono<Void> create(Mono<Book> book, WebExchange exchange) {
		return book.flatMap(b -> {
			if(this.bookDB.putIfAbsent(b.getIsbn(), b) != null) {
				throw new BadRequestException("Book with reference " + b.getIsbn() + " already exist");
			}
			
			exchange.response().headers(headers -> headers
				.status(Status.CREATED)
				.add(Headers.NAME_LOCATION, exchange.request().getPathBuilder().segment(b.getIsbn()).buildPath())
			);
			
			return Mono.empty();
		});
	}
	
	@Override
	public Mono<Void> update(String isbn, Mono<Book> book) {
		return book.flatMap(b -> {
			if(!this.bookDB.containsKey(isbn)) {
				throw new NotFoundException("Book with reference " + isbn + " does not exist");
			}
			this.bookDB.put(isbn, b);
			return Mono.empty();
		});
	}
	
	@Override
	public Flux<Book> list() {
		return Flux.fromIterable(this.bookDB.values());
	}
	
	@Override
	public Mono<Book> get(String isbn) {
		return Mono.fromSupplier(() -> {
			if(!this.bookDB.containsKey(isbn)) {
				throw new NotFoundException("Book with reference " + isbn + " does not exist");
			}
			return this.bookDB.get(isbn);
		});
	}
	
	@Override
	public Mono<Void> delete(String isbn) {
		return Mono.fromRunnable(() -> {
			if(this.bookDB.remove(isbn) == null) {
				throw new NotFoundException("Book with reference " + isbn + " does not exist");
			}
		});
	}
	
	/**
	 * Uploads a book in a file in a multipart form request.
	 * 
	 * @param parts The uploaded parts
	 * @return a list of operation results for the uploaded parts
	 */
	@WebRoute( path = "/upload", method = Method.POST, consumes = MediaTypes.MULTIPART_FORM_DATA, produces = MediaTypes.APPLICATION_JSON)
	Flux<Result> upload(@Body Flux<WebPart> parts) {
		return parts
			.flatMap(part -> part.decoder(Book.class).one())
	        .map(book -> storeBook(book));
	}
	
	/**
	 * Returns book events as Server-sent events.
	 * 
	 * @param events the Server-sent events factory
	 * 
	 * @return a stream of events
	 */
	@WebRoute(path = "/event", method = Method.GET)
	public Publisher<WebResponseBody.SseEncoder.Event<BookEvent>> getBookEvents(@SseEventFactory(MediaTypes.APPLICATION_JSON) WebResponseBody.SseEncoder.EventFactory<BookEvent> events) {
		return Flux.interval(Duration.ofSeconds(1))
			.map(seq -> events.create(
					event -> event
						.id(Long.toString(seq))
						.event("bookEvent")
						.value(new BookEvent("some book event\r\n"))
				)
			);
	}
	
	private Result storeBook(Book book) {
		if(this.bookDB.putIfAbsent(book.getIsbn(), book) == null) {
			return new Result("Book " + book.getIsbn() + " has been stored.");
		}
		return new Result("Book " + book.getIsbn() + " already exists.");
	}
}
