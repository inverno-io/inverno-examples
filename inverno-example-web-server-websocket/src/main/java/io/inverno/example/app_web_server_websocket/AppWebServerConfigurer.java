/*
 * Copyright 2022 Jeremy KUHN
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
package io.inverno.example.app_web_server_websocket;

import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Destroy;
import io.inverno.core.annotation.Init;
import io.inverno.example.app_web_server_websocket.dto.Message;
import io.inverno.mod.base.resource.Resource;
import io.inverno.mod.base.resource.ResourceService;
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.Status;
import io.inverno.mod.web.server.ErrorWebRouter;
import io.inverno.mod.web.server.StaticHandler;
import io.inverno.mod.web.server.WebRouter;
import io.inverno.mod.web.server.WhiteLabelErrorRoutesConfigurer;
import io.inverno.mod.web.server.annotation.WebRoute;
import io.inverno.mod.web.server.annotation.WebRoutes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * <p>
 * The Web server configurer configuring {@code /ws} WebSocket route programmatically and returning the WebSocket HTML client otherwise.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean(visibility = Bean.Visibility.PRIVATE)
@WebRoutes({
	@WebRoute( path = { "/{path:.*}" }, method = { Method.GET } ),
	@WebRoute( path = { "/ws" }, method = { Method.GET } ),
})
public class AppWebServerConfigurer implements WebRouter.Configurer<ExchangeContext>, ErrorWebRouter.Configurer<ExchangeContext> {
	
	private final AppConfiguration configuration;
	
	private final Resource rootResouce;
	
	private Sinks.Many<Message> chatSink;
	
	public AppWebServerConfigurer(AppConfiguration configuration, ResourceService resourceService) {
		this.configuration = configuration;
		this.rootResouce = resourceService.getResource(this.configuration.web_root());
	}
	
	@Init
	public void init() {
		this.chatSink = Sinks.many().multicast().onBackpressureBuffer(16, false);
	}

	@Destroy
	public void destroy() {
		this.chatSink.tryEmitComplete();
	}

	@Override
	public void configure(WebRouter<ExchangeContext> routes) {
		routes
			.route()
				.path("/{path:.*}", true)
				.method(Method.GET)
				.handler(new StaticHandler<>(this.rootResouce))
			.route()
				.path("/ws")
				.method(Method.GET)
				.handler(exchange -> {
					exchange.response()
						.headers(headers -> headers.status(Status.BAD_REQUEST))
						.body().string().value("Not a WebSocket upgrade request");
				})
			// 1. Using a WebSocket route
			.webSocketRoute()
				.path("/ws")
				.subprotocol("json")
				.handler(webSocketExchange -> {
					Flux.from(webSocketExchange.inbound().decodeTextMessages(Message.class)).subscribe(message -> this.chatSink.tryEmitNext(message));
					webSocketExchange.outbound().encodeTextMessages(this.chatSink.asFlux());
				});
			// 2. Using a Web route + explicit WebSocket upgrade
			/*.route()
				.path("/ws")
				.method(Method.GET)
				.handler(exchange -> {
					exchange.webSocket("json")
						.orElseThrow(() -> new InternalServerErrorException("WebSocket not supported"))
						.handler(webSocketExchange -> {
							Flux.from(webSocketExchange.inbound().decodeTextMessages(Message.class)).subscribe(message -> this.chatSink.tryEmitNext(message));
							webSocketExchange.outbound().encodeTextMessages(this.chatSink.asFlux());
						})
						.or(() -> exchange.response()
							.body().string().value("Web socket handshake failed")
						);
				});*/
	}

	@Override
	public void configure(ErrorWebRouter<ExchangeContext> errorRoutes) {
		errorRoutes
			.configureErrorRoutes(new WhiteLabelErrorRoutesConfigurer<>());
	}
}
