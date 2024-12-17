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
package io.inverno.example.app_web_server;

import io.inverno.example.app_web_server.dto.Book;
import io.inverno.mod.web.server.WebExchange;
import io.inverno.mod.web.server.annotation.WebController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p>
 * A Book REST resource exposing CRUD operations.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@WebController(path = "/book")
public interface BookResource extends CRUD<Book> {

	@Override
	Mono<Void> create(Mono<Book> book, WebExchange<?> exchange);
	
	@Override
	Mono<Void> update(String id, Mono<Book> book);
	
	@Override
	Flux<Book> list();
	
	@Override
	<E extends WebContext & InterceptorContext> Mono<Book> get(String id, E context);
	
	@Override
	Mono<Void> delete(String id);
}
