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
package io.inverno.example.web_modular.book;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.inverno.core.annotation.Bean;
import io.inverno.example.web_modular.book.dto.Book;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.http.base.BadRequestException;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.NotFoundException;
import io.inverno.mod.http.base.Status;
import io.inverno.mod.http.base.header.Headers;
import io.inverno.mod.web.server.WebExchange;
import io.inverno.mod.web.server.annotation.Body;
import io.inverno.mod.web.server.annotation.PathParam;
import io.inverno.mod.web.server.annotation.WebController;
import io.inverno.mod.web.server.annotation.WebRoute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The book resource.
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
@Bean
@WebController(path = "/book")
public class BookResource {

	private Map<String, Book> bookDB = new ConcurrentHashMap<>(); 
	
	/**
	 * Creates a book resource.
	 * 
	 * @param book a book
	 * @param exchange the web exchange
	 * 
	 * @return {@inverno.web.status 201} the book resource has been successfully created
	 * @throws BadRequestException A book with the same ISBN reference already exist
	 */
	@WebRoute(method = Method.POST, consumes = MediaTypes.APPLICATION_JSON)
	public Mono<Void> create(@Body Mono<Book> book, WebExchange exchange) throws BadRequestException {
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
	
	/**
	 * Updates a book resource.
	 * 
	 * @param isbn the reference of the book resource to update
	 * @param book the updated book resource
	 * 
	 * @return the book resource has been successfully updated
	 * @throws NotFoundException if the specified reference does not exist
	 */
	@WebRoute(path = "/{isbn}", method = Method.PUT, consumes = MediaTypes.APPLICATION_JSON)
	public Mono<Void> update(@PathParam String isbn, @Body Mono<Book> book) throws NotFoundException {
		return book.flatMap(b -> {
			if(!this.bookDB.containsKey(isbn)) {
				throw new NotFoundException("Book with reference " + isbn + " does not exist");
			}
			this.bookDB.put(isbn, b);
			return Mono.empty();
		});
	}
	
	/**
	 * Returns the list of book resources.
	 * 
	 * @return a list of book resources
	 */
	@WebRoute(method = Method.GET, produces = MediaTypes.APPLICATION_JSON)
	public Flux<Book> list() {
		return Flux.fromIterable(this.bookDB.values());
	}
	
	/**
	 * Returns the book resource identified by the specified ISBN.
	 * 
	 * @param isbn an ISBN
	 * 
	 * @return the requested book resource
	 * @throws NotFoundException if the specified reference does not exist
	 */
	@WebRoute(path = "/{isbn}", method = Method.GET, produces = MediaTypes.APPLICATION_JSON)
	public Mono<Book> get(@PathParam String isbn) throws NotFoundException {
		return Mono.fromSupplier(() -> {
			if(!this.bookDB.containsKey(isbn)) {
				throw new NotFoundException("Book with reference " + isbn + " does not exist");
			}
			return this.bookDB.get(isbn);
		});
	}
	
	/**
	 * Deletes the book resource identified by the specified ISBN.
	 * 
	 * @param isbn an ISBN
	 * 
	 * @return the book resource has been successfully deleted
	 * @throws NotFoundException if the specified reference does not exist
	 */
	@WebRoute(path = "/{isbn}", method = Method.DELETE)
	public Mono<Void> delete(@PathParam String isbn) {
		return Mono.fromRunnable(() -> {
			if(this.bookDB.remove(isbn) == null) {
				throw new NotFoundException("Book with reference " + isbn + " does not exist");
			}
		});
	}
}
