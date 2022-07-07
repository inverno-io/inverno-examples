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
package io.inverno.example.app_web_websocket;

import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Destroy;
import io.inverno.core.annotation.Init;
import io.inverno.example.app_web_websocket.dto.GenericMessage;
import io.inverno.example.app_web_websocket.dto.Message;
import io.inverno.mod.base.Charsets;
import io.inverno.mod.http.server.ExchangeContext;
import io.inverno.mod.http.server.ws.WebSocketMessage;
import io.inverno.mod.web.Web2SocketExchange;
import io.inverno.mod.web.WebExchange;
import io.inverno.mod.web.annotation.WebController;
import io.inverno.mod.web.annotation.WebRoute;
import io.inverno.mod.web.annotation.WebSocketRoute;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean
@WebController(path = "/controller")
public class App_web_websocketWebController {
	
	private Sinks.Many<Message> chatSink;
	
	@Init
	public void init() {
		this.chatSink = Sinks.many().multicast().onBackpressureBuffer(16, false);
	}

	@Destroy
	public void destroy() {
		this.chatSink.tryEmitComplete();
	}
	
	@WebSocketRoute(path = "ws", subprotocol = "json")
	public Flux<Message> ws2(Flux<Message> inbound) {
		inbound.subscribe(message -> this.chatSink.tryEmitNext(message));
		return this.chatSink.asFlux();
	}
	
}
