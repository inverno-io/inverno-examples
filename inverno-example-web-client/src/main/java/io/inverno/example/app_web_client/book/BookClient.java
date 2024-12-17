/*
 * Copyright 2024 Jeremy Kuhn
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
package io.inverno.example.app_web_client.book;

import io.inverno.example.app_web_client.book.dto.Book;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.http.base.BadRequestException;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.NotFoundException;
import io.inverno.mod.web.base.annotation.Body;
import io.inverno.mod.web.base.annotation.PathParam;
import io.inverno.mod.web.client.annotation.WebClient;
import io.inverno.mod.web.client.annotation.WebRoute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p>
 * The declarative Web client exposing CRUD operations on the Book resource.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@WebClient( uri = "conf://book")
public interface BookClient {

	@WebRoute(method = Method.POST, produces = MediaTypes.APPLICATION_JSON)
	Mono<Void> create(@Body Book book) throws BadRequestException;

	@WebRoute(path = "/{isbn}", method = Method.PUT, produces = MediaTypes.APPLICATION_JSON)
	Mono<Void> update(@PathParam String isbn, @Body Book book) throws NotFoundException;

	@WebRoute(method = Method.GET, consumes = MediaTypes.APPLICATION_JSON)
	Flux<Book> list();

	@WebRoute(path = "/{isbn}", method = Method.GET, consumes = MediaTypes.APPLICATION_JSON)
	Mono<Book> get(@PathParam String isbn);

	@WebRoute(path = "/{isbn}", method = Method.DELETE)
	Mono<Void> delete(@PathParam String isbn);
}
