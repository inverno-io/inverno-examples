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

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.netty.buffer.Unpooled;
import io.winterframework.core.annotation.Bean;
import io.winterframework.core.annotation.Bean.Visibility;
import io.winterframework.example.web.dto.Message;
import io.winterframework.mod.base.resource.MediaTypes;
import io.winterframework.mod.web.Method;
import io.winterframework.mod.web.Parameter;
import io.winterframework.mod.web.router.MissingRequiredParameterException;
import io.winterframework.mod.web.router.WebExchange;
import io.winterframework.mod.web.router.WebRouter;
import io.winterframework.mod.web.router.annotation.QueryParam;
import io.winterframework.mod.web.router.annotation.WebRoute;
import io.winterframework.mod.web.router.annotation.WebRouterConfigurer;
import reactor.core.publisher.Flux;

/**
 * @author jkuhn
 *
 */
@WebRouterConfigurer({
	@WebRoute(path = "/"),
	@WebRoute(path = "/get", method = Method.GET),
	@WebRoute(path = "/get/plaintext1", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext2", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext3", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext4", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext5", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext6", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext7", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext/queryParam1", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext/queryParam2", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext/queryParam3", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext/queryParam4", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext/pathParam1/{pathParam}", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext/pathParam2/{pathParam}", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext/headerParam1", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext/headerParam2", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext/cookieParam1", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext/cookieParam2", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext_stream1", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/plaintext_stream2", method = Method.GET, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/get/json1", method = Method.GET, produces = MediaTypes.APPLICATION_JSON),
	@WebRoute(path = "/get/json2", method = Method.GET, produces = MediaTypes.APPLICATION_JSON),
	@WebRoute(path = "/get/json3", method = Method.GET, produces = MediaTypes.APPLICATION_JSON),
	@WebRoute(path = "/get/json_stream1", method = Method.GET, produces = MediaTypes.APPLICATION_JSON),
	@WebRoute(path = "/get/json_stream2", method = Method.GET, produces = MediaTypes.APPLICATION_X_NDJSON),
	@WebRoute(path = "/get/void1", method = Method.GET),
	@WebRoute(path = "/get/void1", method = Method.GET),
	@WebRoute(path = "/post/plaintext1", method = Method.POST, consumes = MediaTypes.TEXT_PLAIN, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/post/plaintext2", method = Method.POST, consumes = MediaTypes.TEXT_PLAIN, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/post/plaintext3", method = Method.POST, consumes = MediaTypes.TEXT_PLAIN, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/post/plaintext4", method = Method.POST, consumes = MediaTypes.TEXT_PLAIN, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/post/plaintext5", method = Method.POST, consumes = MediaTypes.TEXT_PLAIN, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/post/plaintext6", method = Method.POST, consumes = MediaTypes.TEXT_PLAIN, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/post/plaintext7", method = Method.POST, consumes = MediaTypes.TEXT_PLAIN, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/post/json1", method = Method.POST, consumes = MediaTypes.APPLICATION_JSON, produces = MediaTypes.APPLICATION_JSON),
	@WebRoute(path = "/post/json2", method = Method.POST, consumes = MediaTypes.APPLICATION_JSON, produces = MediaTypes.APPLICATION_JSON),
	@WebRoute(path = "/post/json3", method = Method.POST, consumes = MediaTypes.APPLICATION_JSON, produces = MediaTypes.APPLICATION_JSON),
	@WebRoute(path = "/post/json4", method = Method.POST, consumes = MediaTypes.APPLICATION_JSON, produces = MediaTypes.APPLICATION_JSON),
	@WebRoute(path = "/post/json5", method = Method.POST, consumes = MediaTypes.APPLICATION_JSON, produces = MediaTypes.APPLICATION_JSON),
	@WebRoute(path = "/post/json6", method = Method.POST, consumes = MediaTypes.APPLICATION_JSON, produces = MediaTypes.APPLICATION_JSON),
	@WebRoute(path = "/post/json7", method = Method.POST, consumes = MediaTypes.APPLICATION_JSON, produces = MediaTypes.APPLICATION_JSON),
	@WebRoute(path = "/post/form1", method = Method.POST, consumes = MediaTypes.APPLICATION_X_WWW_FORM_URLENCODED, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/post/form2", method = Method.POST, consumes = MediaTypes.APPLICATION_X_WWW_FORM_URLENCODED, produces = MediaTypes.TEXT_PLAIN),
	@WebRoute(path = "/post/multipart1", method = Method.POST, consumes = MediaTypes.APPLICATION_X_WWW_FORM_URLENCODED, produces = MediaTypes.TEXT_PLAIN)
})
//@Bean(visibility = Visibility.PRIVATE)
public class WebControllerRouterConfigurer implements Consumer<WebRouter<WebExchange>> {

	private WebController controller;
	
	public WebControllerRouterConfigurer(WebController controller) {
		this.controller = controller;
	}
	
	@Override
	public void accept(WebRouter<WebExchange> router) {
		router
			// curl --insecure -iv 'http://127.0.0.1:8080/'
			.route().path("/").handler(exchange -> { 
				exchange.response().body().raw().data(this.controller.root());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get'
			.route().path("/get").method(Method.GET).handler(exchange -> {
				exchange.response().body().raw().data(this.controller.get());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext1'
			.route().path("/get/plaintext1").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.get_plaintext1());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext2'			
			.route().path("/get/plaintext2").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.get_plaintext2());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext3'
			.route().path("/get/plaintext3").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().raw().data(this.controller.get_plaintext3());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext4'
			.route().path("/get/plaintext4").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().raw().data(this.controller.get_plaintext4());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext5'
			.route().path("/get/plaintext5").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.get_plaintext5());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext6'
			.route().path("/get/plaintext6").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.get_plaintext6());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext7'
			.route().path("/get/plaintext7").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.get_plaintext7(exchange));
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext/queryParam1?queryParam=abcdef'
			.route().path("/get/plaintext/queryParam1").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				// TODO required? default?
				exchange.response().body().encoder().data(this.controller.get_plaintext_queryParam1(exchange.request().queryParameters().get("queryParam").get().asString()));
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext/queryParam2?queryParam=12345'
			.route().path("/get/plaintext/queryParam2").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				// TODO required? default?
				exchange.response().body().encoder().data(this.controller.get_plaintext_queryParam2(exchange.request().queryParameters().get("queryParam").get().asInteger()));
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext/queryParam3?queryParam=12,34,56&queryParam=78,9'
			.route().path("/get/plaintext/queryParam3").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.get_plaintext_queryParam3(Optional.of(exchange.request().queryParameters().getAll("queryParam").stream()
						.flatMap(param -> param.asListOf(Integer.class).stream())
						.collect(Collectors.toList())).filter(l -> !l.isEmpty()).orElseThrow(() -> new MissingRequiredParameterException("queryParam"))));
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext/queryParam4?queryParam=12,34,56&queryParam=78,9'
			.route().path("/get/plaintext/queryParam4").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.get_plaintext_queryParam4(Optional.of(exchange.request().queryParameters().getAll("queryParam").stream()
						.flatMap(param -> param.asListOf(Integer.class).stream())
						.collect(Collectors.toList())).filter(l -> !l.isEmpty())));
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext/pathParam1/abcdef'
			.route().path("/get/plaintext/pathParam1/{pathParam}").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				// TODO required? default?
				exchange.response().body().encoder().data(this.controller.get_plaintext_pathParam1(exchange.request().pathParameters().get("pathParam").get().asString()));
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext/pathParam2/12345'
			.route().path("/get/plaintext/pathParam2/{pathParam}").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				// TODO required? default?
				exchange.response().body().encoder().data(this.controller.get_plaintext_pathParam2(exchange.request().pathParameters().get("pathParam").get().asInteger()));
			})
			// curl --insecure -iv -H 'headerParam: abcdef' 'http://127.0.0.1:8080/get/plaintext/headerParam1'
			.route().path("/get/plaintext/headerParam1").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				// TODO required? default?
				exchange.response().body().encoder().data(this.controller.get_plaintext_headerParam1(exchange.request().headers().getParameter("headerParam").get().asString()));
			})
			// curl --insecure -iv -H 'headerParam: 12345' 'http://127.0.0.1:8080/get/plaintext/headerParam2'
			.route().path("/get/plaintext/headerParam2").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				// TODO required? default?
				exchange.response().body().encoder().data(this.controller.get_plaintext_headerParam2(exchange.request().headers().getParameter("headerParam").get().asInteger()));
			})
			// curl --insecure -iv -H 'cookie: cookieParam=abcdef' 'http://127.0.0.1:8080/get/plaintext/cookieParam1'
			.route().path("/get/plaintext/cookieParam1").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				// TODO required? default?
				exchange.response().body().encoder().data(this.controller.get_plaintext_cookieParam1(exchange.request().cookies().get("cookieParam").get().asString()));
			})
			// curl --insecure -iv -H 'cookie: cookieParam=12345' 'http://127.0.0.1:8080/get/plaintext/cookieParam2'
			.route().path("/get/plaintext/cookieParam2").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				// TODO required? default?
				exchange.response().body().encoder().data(this.controller.get_plaintext_cookieParam2(exchange.request().cookies().get("cookieParam").get().asInteger()));
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext_stream1'
			.route().path("/get/plaintext_stream1").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.get_plaintext_stream1());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/plaintext_stream2'
			.route().path("/get/plaintext_stream2").method(Method.GET).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().raw().data(this.controller.get_plaintext_stream2());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/json1'
			.route().path("/get/json1").method(Method.GET).produces(MediaTypes.APPLICATION_JSON).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.get_json1());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/json2'
			.route().path("/get/json2").method(Method.GET).produces(MediaTypes.APPLICATION_JSON).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.get_json2());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/json3'
			.route().path("/get/json3").method(Method.GET).produces(MediaTypes.APPLICATION_JSON).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.get_json3());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/json_stream1'
			.route().path("/get/json_stream1").method(Method.GET).produces(MediaTypes.APPLICATION_JSON).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.get_json_stream1());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/json_stream2'
			.route().path("/get/json_stream2").method(Method.GET).produces(MediaTypes.APPLICATION_X_NDJSON).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.get_json_stream2());
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/void1'
			.route().path("/get/void1").method(Method.GET).handler(exchange -> {
				this.controller.get_void1();
				exchange.response().body().empty();
			})
			// curl --insecure -iv 'http://127.0.0.1:8080/get/void2'
			.route().path("/get/void2").method(Method.GET).handler(exchange -> {
				this.controller.get_void2(exchange);
				exchange.response().body().empty();
			})
			// curl --insecure -iv --http1.1 -d 'Hello, world!' -H 'content-type: text/plain' -X POST 'http://127.0.0.1:8080/post/plaintext1'
			.route().path("/post/plaintext1").method(Method.POST).consumes(MediaTypes.TEXT_PLAIN).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(exchange.request().body().get().decoder(String.class).one().map(this.controller::post_plaintext1));
			})
			// curl --insecure -iv --http1.1 -d 'Hello, world!' -H 'content-type: text/plain' -X POST 'http://127.0.0.1:8080/post/plaintext2'
			.route().path("/post/plaintext2").method(Method.POST).consumes(MediaTypes.TEXT_PLAIN).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.post_plaintext2(exchange.request().body().get().decoder(String.class).one()));
			})
			// curl --insecure -iv --http1.1 -d 'Hello, world!' -H 'content-type: text/plain' -X POST 'http://127.0.0.1:8080/post/plaintext3'
			.route().path("/post/plaintext3").method(Method.POST).consumes(MediaTypes.TEXT_PLAIN).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.post_plaintext3(exchange.request().body().get().decoder(String.class).one()));
			})
			// curl --insecure -iv --http1.1 -d 'Hello, world!' -H 'content-type: text/plain' -X POST 'http://127.0.0.1:8080/post/plaintext4'
			.route().path("/post/plaintext4").method(Method.POST).consumes(MediaTypes.TEXT_PLAIN).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.post_plaintext4(exchange.request().body().get().decoder(String.class).one()));
			})
			// curl --insecure -iv --http1.1 -d 'Hello, world!' -H 'content-type: text/plain' -X POST 'http://127.0.0.1:8080/post/plaintext5'
			.route().path("/post/plaintext5").method(Method.POST).consumes(MediaTypes.TEXT_PLAIN).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.post_plaintext5(exchange.request().body().get().decoder(String.class).many()));
			})
			// curl --insecure -iv --http1.1 -d 'Hello, world!' -H 'content-type: text/plain' -X POST 'http://127.0.0.1:8080/post/plaintext6'
			.route().path("/post/plaintext6").method(Method.POST).consumes(MediaTypes.TEXT_PLAIN).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.post_plaintext6(exchange.request().body().get().decoder(String.class).many()));
			})
			// curl --insecure -iv --http1.1 -d 'Hello, world!' -H 'content-type: text/plain' -X POST 'http://127.0.0.1:8080/post/plaintext7'
			.route().path("/post/plaintext7").method(Method.POST).consumes(MediaTypes.TEXT_PLAIN).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.post_plaintext7(exchange.request().body().get().decoder(String.class).many()));
			})
			// curl --insecure -iv --http1.1 -d '{"message":"Hello, world!"}' -H 'content-type: application/json' -X POST 'http://127.0.0.1:8080/post/json1'
			.route().path("/post/json1").method(Method.POST).consumes(MediaTypes.APPLICATION_JSON).produces(MediaTypes.APPLICATION_JSON).handler(exchange -> {
				exchange.response().body().encoder().data(exchange.request().body().get().decoder(Message.class).one().map(this.controller::post_json1));
			})
			// curl --insecure -iv --http1.1 -d '{"message":"Hello, world!"}' -H 'content-type: application/json' -X POST 'http://127.0.0.1:8080/post/json2'
			.route().path("/post/json2").method(Method.POST).consumes(MediaTypes.APPLICATION_JSON).produces(MediaTypes.APPLICATION_JSON).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.post_json2(exchange.request().body().get().decoder(Message.class).one()));
			})
			// curl --insecure -iv --http1.1 -d '{"message":"Hello, world!"}' -H 'content-type: application/json' -X POST 'http://127.0.0.1:8080/post/json3'
			.route().path("/post/json3").method(Method.POST).consumes(MediaTypes.APPLICATION_JSON).produces(MediaTypes.APPLICATION_JSON).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.post_json3(exchange.request().body().get().decoder(Message.class).one()));
			})
			// curl --insecure -iv --http1.1 -d '{"message":"Hello, world!"}' -H 'content-type: application/json' -X POST 'http://127.0.0.1:8080/post/json4'
			.route().path("/post/json4").method(Method.POST).consumes(MediaTypes.APPLICATION_JSON).produces(MediaTypes.APPLICATION_JSON).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.post_json4(exchange.request().body().get().decoder(Message.class).one()));
			})
			// curl --insecure -iv --http1.1 -d '{"message":"Hello, world!"}{"message":"Hallo, welt!"}{"message":"Salut, le monde!"}' -H 'content-type: application/json' -X POST 'http://127.0.0.1:8080/post/json5'
			.route().path("/post/json5").method(Method.POST).consumes(MediaTypes.APPLICATION_JSON).produces(MediaTypes.APPLICATION_JSON).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.post_json5(exchange.request().body().get().decoder(Message.class).many()));
			})
			// curl --insecure -iv --http1.1 -d '{"message":"Hello, world!"}{"message":"Hallo, welt!"}{"message":"Salut, le monde!"}' -H 'content-type: application/json' -X POST 'http://127.0.0.1:8080/post/json6'
			.route().path("/post/json6").method(Method.POST).consumes(MediaTypes.APPLICATION_JSON).produces(MediaTypes.APPLICATION_JSON).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.post_json6(exchange.request().body().get().decoder(Message.class).many()));
			})
			// curl --insecure -iv --http1.1 -d '{"message":"Hello, world!"}{"message":"Hallo, welt!"}{"message":"Salut, le monde!"}' -H 'content-type: application/json' -X POST 'http://127.0.0.1:8080/post/json7'
			.route().path("/post/json7").method(Method.POST).consumes(MediaTypes.APPLICATION_JSON).produces(MediaTypes.APPLICATION_JSON).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.post_json7(exchange.request().body().get().decoder(Message.class).many()));
			})
			// curl --insecure -iv --http1.1 -d 'formParam1=tata' -X POST http://127.0.0.1:8080/post/form1
			.route().path("/post/form1").method(Method.POST).consumes(MediaTypes.APPLICATION_X_WWW_FORM_URLENCODED).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				
				
				Flux.from(exchange.request().body().get().urlEncoded().parameters())
				.collectMultimap(Parameter::getName);
				
				
				exchange.response().body().encoder().data(
					Flux.from(exchange.request().body().get().urlEncoded().parameters())
						.collectMultimap(Parameter::getName)
						.map(parameters -> this.controller.post_form1(parameters.get("formParam1").stream().findFirst().map(parameter -> parameter.asString()).orElseThrow(() -> new MissingRequiredParameterException("formParam1"))))
				);
			})
			// curl --insecure -iv --http1.1 -d 'formParam1=tata&formParam2=123456' -X POST http://127.0.0.1:8080/post/form2
			.route().path("/post/form2").method(Method.POST).consumes(MediaTypes.APPLICATION_X_WWW_FORM_URLENCODED).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(Flux.from(exchange.request().body().get().urlEncoded().parameters()).collectMultimap(Parameter::getName).map(formParameters -> this.controller.post_form2(formParameters.get("formParam1").stream().findFirst().map(parameter -> parameter.as(String.class)).orElseThrow(() -> new MissingRequiredParameterException("formParam1")), Optional.of(formParameters.get("formParam2").stream().flatMap(parameter -> parameter.asListOf(Integer.class).stream()).collect(Collectors.toList())).filter(l -> !l.isEmpty()).orElseThrow(() -> new MissingRequiredParameterException("formParam2")))));
			})
			// curl --insecure -iv --http1.1 --form 'multipartParam1=abcd' --form 'multipartParam2=123456' --form 'multipartParam3=TOTO' -X POST http://127.0.0.1:8080/post/multipart1
			.route().path("/post/multipart1").method(Method.POST).consumes(MediaTypes.MULTIPART_FORM_DATA).produces(MediaTypes.TEXT_PLAIN).handler(exchange -> {
				exchange.response().body().encoder().data(this.controller.post_multipart1(Flux.from(exchange.request().body().get().multipart().parts())));
			})
			;
		
//		@WebRoute(path = "/post/multipart1", method = Method.POST, consumes = MediaTypes.APPLICATION_X_WWW_FORM_URLENCODED, produces = MediaTypes.TEXT_PLAIN)
//		public Flux<String> post_multipart2(@Body Flux<Part> multipart) {
//			// TODO here we must release the bytebuf of each part
//			// TODO we must be able to decode part raw data just like body raw data 
//			return multipart.flatMap(part -> Flux.from(part.data()).doOnNext(ByteBuf::release).then(Mono.just("Received: " + part.getName())));
//		}
	}
}
