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

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.AsciiString;
import io.winterframework.core.annotation.Bean;
import io.winterframework.example.web.dto.Message;
import io.winterframework.mod.base.converter.ReactiveConverter;
import io.winterframework.mod.web.WebException;
import io.winterframework.mod.web.server.Exchange;
import io.winterframework.mod.web.server.ExchangeHandler;
import reactor.core.publisher.Mono;

/**
 * @author jkuhn
 *
 */
@Bean
public class JsonHandler  implements ExchangeHandler<Exchange> {

	private static final Mono<Message> JSON_MONO = Mono.fromSupplier(new JsonSupplier());
	
	private static final AsciiString STATIC_SERVER = AsciiString.cached("winter");
	
	private ReactiveConverter<ByteBuf, Object> jsonConverter;
	
	public JsonHandler(ReactiveConverter<ByteBuf, Object> jsonConverter) {
		this.jsonConverter = jsonConverter;
	}
	
	private static class JsonSupplier implements Supplier<Message> {
		@Override
		public Message get() {
			return new Message("Hello World");
		}
	}
	
	@Override
	public void handle(Exchange exchange) throws WebException {
		exchange.response().headers(h -> h
				.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
				.add(HttpHeaderNames.SERVER, STATIC_SERVER)
			).body().raw().data(this.jsonConverter.encodeOne(JSON_MONO));
	}
}
