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
package io.inverno.example.app_http_server_websocket;

import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Destroy;
import io.inverno.core.annotation.Init;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.base.resource.PathResource;
import io.inverno.mod.base.resource.Resource;
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.base.HttpException;
import io.inverno.mod.http.base.ws.WebSocketFrame;
import io.inverno.mod.http.server.ErrorExchange;
import io.inverno.mod.http.server.Exchange;
import io.inverno.mod.http.server.ServerController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * <p>
 * The HTTP server controller negotiating the WebSocket connection when receiving a {@code /ws} request and responding with the WebSocket HTML client otherwise.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean
public class AppWebServerController implements ServerController<ExchangeContext, Exchange<ExchangeContext>, ErrorExchange<ExchangeContext>> {

	private static final Logger LOGGER = LogManager.getLogger(AppWebServerController.class);
	
	private final Resource index;
	
	// Solution 0: retain duplicate frame
	private Sinks.Many<WebSocketFrame> chatSink;
	
	// solution 1: retain duplicate frame content
//	private Sinks.Many<ByteBuf> chatSink;
	
	// Solution 2: recopy frame content
//	private Sinks.Many<String> chatSink;	
	
	
	public AppWebServerController(AppConfiguration configuration) {
		this.index = new PathResource(configuration.index());
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
	public void handle(Exchange<ExchangeContext> exchange) throws HttpException {
		if(exchange.request().getPath().equals("/ws")) {
			exchange.webSocket().ifPresentOrElse(
				websocket -> websocket
					.handler(webSocketExchange -> {
						// Solution 0: duplicate and retain the frame => memory is not copied
						webSocketExchange.outbound().frames(factory -> this.chatSink.asFlux().map(WebSocketFrame::retainedDuplicate));
						Flux.from(webSocketExchange.inbound().frames())
							.subscribe(frame -> {
								LOGGER.info("Broadcasting frame: kind = " + frame.getKind() + ", size = " + frame.getRawData().readableBytes() + ", final = " + frame.isFinal());
								this.chatSink.tryEmitNext(frame);
								frame.release();
							});
						
						// Solution 1: duplicate and retain the frame content which is also not copied into memory
						/*webSocketExchange.outbound().frames(factory -> this.chatSink.asFlux().map(data -> factory.text(data.retainedDuplicate())));
						Flux.from(webSocketExchange.inbound().frames())
							.subscribe(frame -> {
								this.chatSink.tryEmitNext(frame.getBinaryData());
								frame.release();
							});*/
						
						// Solution 2: maybe more obvious but less performant since the frame content is copied into memory multiple times
						/*webSocketExchange.outbound().frames(factory -> this.chatSink.asFlux().map(factory::text));
						Flux.from(webSocketExchange.inbound().frames())
							.subscribe(frame -> {
								this.chatSink.tryEmitNext(frame.getTextData());
								frame.release();
							});*/
					})
					.or(() -> exchange.response()
						.body().string().value("Web socket handshake failed")
					),
				() -> exchange.response()
					.body().string().value("WebSocket not supported")
			);
		}
		else {
			exchange.response()
				.headers(headers -> headers.contentType(MediaTypes.TEXT_HTML))
				.body().resource().value(this.index);
		}
	}
}
