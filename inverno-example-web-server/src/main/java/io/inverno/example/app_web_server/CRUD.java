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

import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.http.base.BadRequestException;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.NotFoundException;
import io.inverno.mod.web.base.annotation.Body;
import io.inverno.mod.web.base.annotation.PathParam;
import io.inverno.mod.web.server.WebExchange;
import io.inverno.mod.web.server.annotation.WebRoute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p>
 * Basic CRUD REST resource.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 * @param <T> The type of the resource
 */
public interface CRUD<T> {

	/**
	 * Creates a resource.
	 * 
	 * @param resource the resource to create
	 * @param exchange the underlying Web exchange
	 * 
	 * @return {@inverno.web.status 201} the resource has been successfully created
	 * @throws if the resource already exists
	 */
	@WebRoute(method = Method.POST, consumes = MediaTypes.APPLICATION_JSON)
	Mono<Void> create(@Body Mono<T> resource, WebExchange<?> exchange) throws BadRequestException;
	
	/**
	 * Updates a resource.
	 * 
	 * @param id the if of the resource to update
	 * @param resource the updated resource
	 * 
	 * @return the resource has been successfully updated
	 * @throws NotFoundException if there's no resource with the specified id
	 */
	@WebRoute(path = "/{id}", method = Method.PUT, consumes = MediaTypes.APPLICATION_JSON)
	Mono<Void> update(@PathParam String id, @Body Mono<T> resource) throws NotFoundException;
	
	/**
	 * Returns the list of resources.
	 * 
	 * @return a list of resources
	 */
	@WebRoute(method = Method.GET, produces = MediaTypes.APPLICATION_JSON)
	Flux<T> list();
	
	/**
	 * Returns the resource identified by the specified id.
	 * 
	 * @param id      an id
	 * @param context the context
	 * @param <E>     the context type
	 * 
	 * @return the requested resource
	 * @throws NotFoundException if there's no resource with the specified id
	 */
	@WebRoute(path = "/{id}", method = Method.GET, produces = MediaTypes.APPLICATION_JSON)
	<E extends WebContext & InterceptorContext> Mono<T> get(@PathParam String id, E context);
	
	/**
	 * Deletes the resource identified by the specified id.
	 * 
	 * @param id an id
	 * 
	 * @return the resource has been successfully deleted
	 * @throws NotFoundException if there's no resource with the specified id
	 */
	@WebRoute(path = "/{id}", method = Method.DELETE)
	Mono<Void> delete(@PathParam String id);
}
