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

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.winterframework.example.web.dto.Message;
import io.winterframework.mod.base.Charsets;
import io.winterframework.mod.base.resource.MediaTypes;
import io.winterframework.mod.web.Method;
import io.winterframework.mod.web.Status;
import io.winterframework.mod.web.router.WebExchange;
import io.winterframework.mod.web.router.annotation.Body;
import io.winterframework.mod.web.router.annotation.CookieParam;
import io.winterframework.mod.web.router.annotation.HeaderParam;
import io.winterframework.mod.web.router.annotation.PathParam;
import io.winterframework.mod.web.router.annotation.QueryParam;
import io.winterframework.mod.web.router.annotation.WebRoute;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author jkuhn
 *
 */
@io.winterframework.mod.web.router.annotation.WebController
public class WebController {

	@WebRoute(path = "/")
	public String root() {
		return "Hello, World!";
	}
	
	@WebRoute(path = "/get", method = Method.GET)
	public String get() {
		return "Hello, World!";
	}
	
	@WebRoute(path = "/get/plaintext1", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public String get_plaintext1() {
		return "Hello, World!";
	}
	
	@WebRoute(path = "/get/plaintext2", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public Mono<String> get_plaintext2() {
		return Mono.just("Hello, World!");
	}
	
	@WebRoute(path = "/get/plaintext3", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public ByteBuf get_plaintext3() {
		return Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello, World!", Charsets.DEFAULT));
	}

	@WebRoute(path = "/get/plaintext4", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public Mono<ByteBuf> get_plaintext4() {
		return Mono.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello, World!", Charsets.DEFAULT)));
	}
	
	// There's a choice to make here: chunked or not...
	// - if not we have to concat all Strings but since we actually consider primitives, we should fist convert primitive to ByteBuf and concat the resulting list of Bytebuf
	// => we must rely on a body encoder for TEXT/PLAIN media type  
	@WebRoute(path = "/get/plaintext5", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public List<String> get_plaintext5() {
		return List.of("Hello", " World!");
	}
	
	// There's a choice to make here: chunked or not...
	// - if not we have to concat all ByteBuf...
	// => we must rely on a body encoder for TEXT/PLAIN media type
	@WebRoute(path = "/get/plaintext6", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public List<ByteBuf> get_plaintext6() {
		return List.of(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello", Charsets.DEFAULT)), Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(" World!", Charsets.DEFAULT)));
	}
	
	@WebRoute(path = "/get/plaintext7", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public String get_plaintext7(WebExchange exchange) {
		exchange.response().headers(h -> h.status(Status.CREATED).set("test", "test"));
		return "Hello, World!";
	}
	
	@WebRoute(path = "/get/plaintext/queryParam1", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public String get_plaintext_queryParam1(@QueryParam String queryParam) {
		return "Query param: " + queryParam;
	}
	
	@WebRoute(path = "/get/plaintext/queryParam2", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public String get_plaintext_queryParam2(@QueryParam int queryParam) {
		return "Query param: " + queryParam;
	}
	
	@WebRoute(path = "/get/plaintext/queryParam3", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public String get_plaintext_queryParam3(@QueryParam List<Integer> queryParam) {
		return "Query param: " + queryParam.stream().map(i -> i.toString()).collect(Collectors.joining(", "));
	}
	
	@WebRoute(path = "/get/plaintext/pathParam1/{pathParam}", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public String get_plaintext_pathParam1(@PathParam String pathParam) {
		return "Path param: " + pathParam;
	}
	
	@WebRoute(path = "/get/plaintext/pathParam2/{pathParam}", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public String get_plaintext_pathParam2(@PathParam int pathParam) {
		return "Path param: " + pathParam;
	}
	
	@WebRoute(path = "/get/plaintext/headerParam1", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public String get_plaintext_headerParam1(@HeaderParam String headerParam) {
		return "Header param: " + headerParam;
	}
	
	@WebRoute(path = "/get/plaintext/headerParam2", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public String get_plaintext_headerParam2(@HeaderParam int headerParam) {
		return "Header param: " + headerParam;
	}
	
	@WebRoute(path = "/get/plaintext/cookieParam1", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public String get_plaintext_cookieParam1(@CookieParam String cookieParam) {
		return "Cookie param: " + cookieParam;
	}
	
	@WebRoute(path = "/get/plaintext/cookieParam2", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public String get_plaintext_cookieParam2(@CookieParam int cookieParam) {
		return "Cookie param: " + cookieParam;
	}

	@WebRoute(path = "/get/plaintext_stream1", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public Flux<String> get_plaintext_stream1() {
		return Flux.just("Hello, ", "World!").delayElements(Duration.ofSeconds(1));
	}
	
	@WebRoute(path = "/get/plaintext_stream2", method = Method.GET, produces = MediaTypes.TEXT_PLAIN)
	public Flux<ByteBuf> get_plaintext_stream2() {
		return Flux.just(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello, ", Charsets.DEFAULT)), Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("World!", Charsets.DEFAULT))).delayElements(Duration.ofSeconds(1));
	}

	@WebRoute(path = "/get/json1", method = Method.GET, produces = MediaTypes.APPLICATION_JSON)
	public Message get_json1() {
		return new Message("Hello, World!");
	}
	
	@WebRoute(path = "/get/json2", method = Method.GET, produces = MediaTypes.APPLICATION_JSON)
	public Mono<Message> get_json2() {
		return Mono.just(new Message("Hello, World!"));
	}
	
	@WebRoute(path = "/get/json3", method = Method.GET, produces = MediaTypes.APPLICATION_JSON)
	public List<Message> get_json3() {
		return List.of(new Message("Hello, "), new Message("World"), new Message("!"));
	}
	
	@WebRoute(path = "/get/json_stream1", method = Method.GET, produces = MediaTypes.APPLICATION_JSON)
	public Flux<Message> get_json_stream1() {
		return Flux.just(new Message("Hello, "), new Message("World"), new Message("!")).delayElements(Duration.ofSeconds(1));
	}
	
	@WebRoute(path = "/get/json_stream2", method = Method.GET, produces = MediaTypes.APPLICATION_X_NDJSON)
	public Flux<Message> get_json_stream2() {
		return Flux.just(new Message("Hello, "), new Message("World"), new Message("!")).delayElements(Duration.ofSeconds(1));
	}
	
	@WebRoute(path = "/get/void1", method = Method.GET)
	public void get_void1() {
		// do something 
	}
	
	@WebRoute(path = "/get/void2", method = Method.GET)
	public void get_void2(WebExchange exchange) {
		exchange.response().headers(h -> h.status(201).add("test", "test"));
	}
	
	// In case we only have one argument, we could say @Body is optional since we can assume the argument is the body by convention
	@WebRoute(path = "/post/plaintext1", method = Method.POST, consumes = MediaTypes.TEXT_PLAIN, produces = MediaTypes.TEXT_PLAIN)
	public String post_plaintext1(@Body String body) {
		return "Received: " + body;
	}
	
	// We can't block the input, so we can't really link the input with the output => we must return Publishers
	@WebRoute(path = "/post/plaintext2", method = Method.POST, consumes = MediaTypes.TEXT_PLAIN, produces = MediaTypes.TEXT_PLAIN)
	public String post_plaintext2(@Body Mono<String> body) {
		return "Received: " + body;
	}
	
	@WebRoute(path = "/post/plaintext3", method = Method.POST, consumes = MediaTypes.TEXT_PLAIN, produces = MediaTypes.TEXT_PLAIN)
	public Mono<String> post_plaintext3(@Body Mono<String> body) {
		return body.map(s -> "Received: " + s);
	}
	
	@WebRoute(path = "/post/plaintext4", method = Method.POST, consumes = MediaTypes.TEXT_PLAIN, produces = MediaTypes.TEXT_PLAIN)
	public Flux<String> post_plaintext4(@Body Mono<String> body) {
		return body.flatMapMany(s -> Flux.fromStream(s.chars().mapToObj(c -> Character.valueOf((char)c)).map(c -> c + "|")));
	}
	
	// We can't block the input, so we can't really link the input with the output => we must return Publishers
	@WebRoute(path = "/post/plaintext5", method = Method.POST, consumes = MediaTypes.TEXT_PLAIN, produces = MediaTypes.TEXT_PLAIN)
	public String post_plaintext5(@Body Flux<String> body) {
		return "Received: " + body;
	}
	
	@WebRoute(path = "/post/plaintext6", method = Method.POST, consumes = MediaTypes.TEXT_PLAIN, produces = MediaTypes.TEXT_PLAIN)
	public Mono<String> post_plaintext6(@Body Flux<String> body) {
		return body.collectList().map(l -> "Received: " + l);
	}
	
	@WebRoute(path = "/post/plaintext7", method = Method.POST, consumes = MediaTypes.TEXT_PLAIN, produces = MediaTypes.TEXT_PLAIN)
	public Flux<String> post_plaintext7(@Body Flux<String> body) {
		return body.map(s -> "Received: " + s + ", ");
	}
	
	@WebRoute(path = "/post/json1", method = Method.POST, consumes = MediaTypes.APPLICATION_JSON, produces = MediaTypes.APPLICATION_JSON)
	public Message post_json1(@Body Message body) {
		return new Message("Received: " + body.getMessage());
	}
	
	@WebRoute(path = "/post/json2", method = Method.POST, consumes = MediaTypes.APPLICATION_JSON, produces = MediaTypes.APPLICATION_JSON)
	public Message post_json2(@Body Mono<Message> body) {
		return new Message("Received: " + body);
	}
	
	@WebRoute(path = "/post/json3", method = Method.POST, consumes = MediaTypes.APPLICATION_JSON, produces = MediaTypes.APPLICATION_JSON)
	public Mono<Message> post_json3(@Body Mono<Message> body) {
		return body.map(m -> new Message("Received: " + m.getMessage()));
	}
	
	@WebRoute(path = "/post/json4", method = Method.POST, consumes = MediaTypes.APPLICATION_JSON, produces = MediaTypes.APPLICATION_JSON)
	public Flux<Message> post_json4(@Body Mono<Message> body) {
		return body.flatMapIterable(m -> List.of(new Message("Received: " + m.getMessage()), new Message("Added this message")));
	}
	
	@WebRoute(path = "/post/json5", method = Method.POST, consumes = MediaTypes.APPLICATION_JSON, produces = MediaTypes.APPLICATION_JSON)
	public Message post_json5(@Body Flux<Message> body) {
		return new Message("Received: " + body);
	}
	
	@WebRoute(path = "/post/json6", method = Method.POST, consumes = MediaTypes.APPLICATION_JSON, produces = MediaTypes.APPLICATION_JSON)
	public Mono<Message> post_json6(@Body Flux<Message> body) {
		return body.collectList().map(l -> new Message("Received: " + l.stream().map(Message::getMessage).collect(Collectors.joining(", "))));
	}
	
	@WebRoute(path = "/post/json7", method = Method.POST, consumes = MediaTypes.APPLICATION_JSON, produces = MediaTypes.APPLICATION_JSON)
	public Flux<Message> post_json7(@Body Flux<Message> body) {
		return body.map(m -> new Message("Received: " + m.getMessage()));
	}
}
