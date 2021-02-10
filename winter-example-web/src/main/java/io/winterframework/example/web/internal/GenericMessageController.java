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
package io.winterframework.example.web.internal;

import io.winterframework.example.web.dto.GenericMessage;
import io.winterframework.mod.base.resource.MediaTypes;
import io.winterframework.mod.web.Method;
import io.winterframework.mod.web.router.WebExchange;
import io.winterframework.mod.web.router.annotation.Body;
import io.winterframework.mod.web.router.annotation.PathParam;
import io.winterframework.mod.web.router.annotation.WebRoute;

/**
 * @author jkuhn
 *
 */
public interface GenericMessageController<A> {

	@WebRoute( path = "", method = Method.POST, produces = MediaTypes.APPLICATION_JSON)
	void createMessage(@Body GenericMessage<A> message, WebExchange exchange);
	
	@WebRoute( path = "{id}", method = Method.GET, produces = MediaTypes.APPLICATION_JSON)
	GenericMessage<A> getMessage(@PathParam int id);
	
	@WebRoute( path = "{id}", method = Method.PUT, produces = MediaTypes.APPLICATION_JSON)
	GenericMessage<A> updateMessage(@PathParam int id, @Body GenericMessage<A> message);
	
	@WebRoute( path = "{id}", method = Method.DELETE)
	void deleteMessage(@PathParam int id);
}
