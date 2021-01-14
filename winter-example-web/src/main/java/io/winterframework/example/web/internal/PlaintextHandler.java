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
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.AsciiString;
import io.winterframework.core.annotation.Bean;
import io.winterframework.mod.web.Charsets;
import io.winterframework.mod.web.WebException;
import io.winterframework.mod.web.server.Exchange;
import io.winterframework.mod.web.server.ExchangeHandler;
import reactor.core.publisher.Mono;

/**
 * @author jkuhn
 *
 */
@Bean
public class PlaintextHandler implements ExchangeHandler<Exchange> {

	private static final byte[] STATIC_PLAINTEXT = "Hello, World!".getBytes(Charsets.UTF_8);
	private static final ByteBuf STATIC_PLAINTEXT_BYTEBUF;
	private static final int STATIC_PLAINTEXT_LEN = STATIC_PLAINTEXT.length;

	private static final AsciiString STATIC_SERVER = AsciiString.cached("winter");
	
	static {
		ByteBuf tmpBuf = Unpooled.directBuffer(STATIC_PLAINTEXT_LEN);
		tmpBuf.writeBytes(STATIC_PLAINTEXT);
		STATIC_PLAINTEXT_BYTEBUF = Unpooled.unreleasableBuffer(tmpBuf);
	}
	
	private static final CharSequence PLAINTEXT_CLHEADER_VALUE = AsciiString.cached(String.valueOf(STATIC_PLAINTEXT_LEN));
	
	private static class PlaintextSupplier implements Supplier<ByteBuf> {
		@Override
		public ByteBuf get() {
			return STATIC_PLAINTEXT_BYTEBUF.duplicate();
		}
	}
	
	private static final Mono<ByteBuf> PLAIN_TEXT_MONO = Mono.fromSupplier(new PlaintextSupplier());

	@Override
	public void handle(Exchange exchange) throws WebException {
		exchange.response()
			.headers(h -> h
				.add(HttpHeaderNames.CONTENT_LENGTH, PLAINTEXT_CLHEADER_VALUE)
				.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
				.add(HttpHeaderNames.SERVER, STATIC_SERVER)
			)
			.body().raw().data(PLAIN_TEXT_MONO);
	}

}
