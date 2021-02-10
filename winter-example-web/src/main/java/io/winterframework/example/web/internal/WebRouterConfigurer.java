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

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.AsciiString;
import io.winterframework.core.annotation.Bean;
import io.winterframework.core.annotation.Bean.Visibility;
import io.winterframework.example.web.ServerConfiguration;
import io.winterframework.example.web.dto.Message;
import io.winterframework.example.web.dto.Person;
import io.winterframework.mod.base.Charsets;
import io.winterframework.mod.base.resource.MediaTypes;
import io.winterframework.mod.base.resource.ResourceService;
import io.winterframework.mod.web.InternalServerErrorException;
import io.winterframework.mod.web.Method;
import io.winterframework.mod.web.WebException;
import io.winterframework.mod.web.router.StaticHandler;
import io.winterframework.mod.web.router.WebExchange;
import io.winterframework.mod.web.router.WebRouter;
import io.winterframework.mod.web.server.Exchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author jkuhn
 *
 */
//@Bean( visibility = Visibility.PRIVATE)
public class WebRouterConfigurer implements Consumer<WebRouter<WebExchange>> {
	
	private ServerConfiguration configuration;
	
	private ResourceService resourceService;
	
	public WebRouterConfigurer(ServerConfiguration configuration, ResourceService resourceService) {
		this.configuration = configuration;
		this.resourceService = resourceService;
	}
	
	@Override
	public void accept(WebRouter<WebExchange> router) {
		router
			.route().path("/plaintext").method(Method.GET).handler(this::plaintext)
			.route().path("/json").method(Method.GET).produces(MediaTypes.APPLICATION_JSON).handler(this::json)
			.route().path("/echo").method(Method.POST).handler(this::echo)
			.route().path("/json").method(Method.POST).consumes(MediaTypes.APPLICATION_JSON).handler(this::readJson)
			.route().path("/json.rw").method(Method.POST).consumes(MediaTypes.APPLICATION_JSON).produces(MediaTypes.APPLICATION_JSON).handler(this::readWriteJson)
			.route().path("/stream").method(Method.GET).handler(this::stream)
			.route().path("/static/{path:.*}").method(Method.GET).handler(new StaticHandler(this.resourceService.get(this.configuration.web_root().toUri())))
			.route().path("/queryParams").method(Method.GET).handler(this::queryParams)
			.route().path("/jsonMap").method(Method.POST).consumes(MediaTypes.APPLICATION_JSON).produces(MediaTypes.TEXT_PLAIN).handler(this::jsonMap);
	}
	
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
	
	private void plaintext(Exchange exchange) {
		exchange.response()
			.headers(h -> h
				.add(HttpHeaderNames.CONTENT_LENGTH, PLAINTEXT_CLHEADER_VALUE)
				.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
				.add(HttpHeaderNames.SERVER, STATIC_SERVER)
			)
			.body().raw().stream(PLAIN_TEXT_MONO);
	}
	
	private void json(WebExchange exchange) throws WebException {
		exchange.response().body().encoder().value(new Message("Hello, World!"));
	}
	
	private void echo(Exchange exchange) {
		exchange.response()
			.headers(headers -> headers.status(200).contentType("text/plain"))
			.body()
				.raw()
				.stream(
					exchange.request().body()
						.map(body -> body.raw().stream())	
						.orElse(Flux.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("=== Empty ===", Charsets.UTF_8))))
			);
	}
	
	private void readJson(WebExchange exchange) {
		exchange.response()
			.headers(h -> h
				.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
			)
			.body()
				.raw()
				.stream(
					exchange.request().body().get()
						.decoder(Person.class)
						.one()
						.map(p -> Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello " + p.getFirstname() + " " +p.getName() + ", you are " + p.getAge() + " years old", Charsets.DEFAULT)))
				);
	}
	
	private void readWriteJson(WebExchange exchange) {
		exchange.response().body()
			.encoder()
			.value(
				exchange.request().body().get()
					.decoder(Person.class)
					.one()
					.doOnNext(p -> p.setFirstname("Bob"))
			);
	}
	
	private void stream(Exchange exchange) {
		exchange.response()
			.headers(headers -> headers
				.status(200)
				.contentType("text/plain; charset=\"UTF-8\"")
				.add("test", "1235")
			)
			.body().raw().stream(
				Flux.just(
					Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response A, \r\n", Charsets.UTF_8)),
					Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response B, \r\n", Charsets.UTF_8)),
					Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response C, \r\n", Charsets.UTF_8)),
					Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response D, \r\n", Charsets.UTF_8)),
					Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Response E \r\n", Charsets.UTF_8))
				)
				.delayElements(Duration.ofMillis(1000))
			);
	}
	
	private Map<String, Person> mapStringPerson;

	@SuppressWarnings("unchecked")
	private void jsonMap(WebExchange exchange) {
		try {
			Type mapStringPersonType = this.getClass().getDeclaredField("mapStringPerson").getGenericType();
			
			exchange.response().body()
				.encoder()
				.value(exchange.request().body().get().decoder(mapStringPersonType).one().map(obj -> {
					return ((Map<String, Person>)obj).entrySet().stream().map(e -> e.getKey() + " -> " + e.getValue().getAge()).collect(Collectors.joining(", "));
				}));
		} catch (NoSuchFieldException | SecurityException e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	private void queryParams(WebExchange exchange) {
		exchange.response().body()
			.raw()
			.value(
				Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(exchange.request().queryParameters().getAll("queryParam").stream().map(parameter -> parameter.asString()).collect(Collectors.joining("|")), Charsets.DEFAULT))
			);
	}
	
	private void sse(WebExchange exchange) {
		
//		exchange.response().body().sse2().from(
//			(events, data) -> Flux.interval(Duration.ofSeconds(1))
//				.map(seq -> events.apply(
//						event -> event
//							.id(Long.toString(seq))
//							.event("periodic-event")
//							.comment("some comment \n on mutliple lines")
//							.value(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("SSE - " + LocalTime.now().toString() + "\r\n", Charsets.UTF_8)))
//					)
//				)
//				.doOnNext(evt -> System.out.println("Emit sse"))
//		);
		
		exchange.response().body().<Message>sseEncoder(MediaTypes.APPLICATION_JSON, Message.class).from(
			(events, data) -> Flux.interval(Duration.ofSeconds(1))
				.map(seq -> events.create(
						event -> event
							.id(Long.toString(seq))
							.event("periodic-event")
							.comment("some comment \n on mutliple lines")
							.value(new Message("SSE - " + LocalTime.now().toString() + "\r\n"))
					)
				)
				.doOnNext(evt -> System.out.println("Emit sse"))
		);
	}
}
