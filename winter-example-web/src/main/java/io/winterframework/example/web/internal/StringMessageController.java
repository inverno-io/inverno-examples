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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.winterframework.core.annotation.Bean;
import io.winterframework.example.web.dto.GenericMessage;
import io.winterframework.mod.base.net.URIs;
import io.winterframework.mod.web.NotFoundException;
import io.winterframework.mod.web.Status;
import io.winterframework.mod.web.header.Headers;
import io.winterframework.mod.web.router.WebExchange;
import io.winterframework.mod.web.router.annotation.WebController;

/**
 * @author jkuhn
 *
 */
@Bean
@WebController("/message/string")
public class StringMessageController  implements GenericMessageController<String> {

	private AtomicInteger index = new AtomicInteger();
	
	// Note that in real life calls to DB must be non-blocking
	private Map<Integer, GenericMessage<String>> data = new ConcurrentHashMap<>();
	
	// curl -iv -d '{"@type":"string","message":"Hello, world!"}' -H 'content-type: application/json' -X POST http://127.0.0.1:8080/message/string
	@Override
	public void createMessage(GenericMessage<String> message, WebExchange exchange) {
		message.setId(this.index.getAndIncrement());
		this.data.put(message.getId(), message);
		exchange.response().headers().status(Status.CREATED).add(Headers.NAME_LOCATION, URIs.uri(exchange.request().headers().getPath()).segment(Integer.toString(message.getId())).buildPath());
	}

	// curl -iv http://127.0.0.1:8080/message/string/0
	@Override
	public GenericMessage<String> getMessage(int id) {
		if(!this.data.containsKey(id)) {
			throw new NotFoundException();
		}
		return this.data.get(id);
	}

	// curl -iv -d '{"@type":"string","message":"Hallo, welt!"}' -H 'content-type: application/json' -X PUT http://127.0.0.1:8080/message/string/0
	@Override
	public GenericMessage<String> updateMessage(int id, GenericMessage<String> message) {
		if(!this.data.containsKey(id)) {
			throw new NotFoundException();
		}
		GenericMessage<String> storedMessage = this.data.get(id);
		storedMessage.setMessage(message.getMessage());
		return storedMessage;
	}

	// curl -iv -X DELETE http://127.0.0.1:8080/message/string/0
	@Override
	public void deleteMessage(int id) {
		if(!this.data.containsKey(id)) {
			throw new NotFoundException();
		}
		this.data.remove(id);
	}
}
