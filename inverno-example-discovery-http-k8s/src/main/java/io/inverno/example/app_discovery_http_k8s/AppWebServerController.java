/*
 * Copyright 2024 Jeremy Kuhn
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
package io.inverno.example.app_discovery_http_k8s;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.base.HttpException;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.web.base.annotation.PathParam;
import io.inverno.mod.web.client.WebClient;
import io.inverno.mod.web.server.WebExchange;
import io.inverno.mod.web.server.annotation.WebController;
import io.inverno.mod.web.server.annotation.WebRoute;
import io.netty.buffer.ByteBuf;
import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * <p>
 * The Web server controller defining a route acting as a gateway to other services.
 * </p>
 *
 * <p>
 * The route handler basically extracts a service URI from the request path and authority and delegates the request processing to the Web client. Under the hood the Web client will resolve and request
 * the matching service.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean
@WebController
public class AppWebServerController {

	private final WebClient<? extends ExchangeContext> webClient;

	public AppWebServerController(WebClient<? extends ExchangeContext> webClient) {
		this.webClient = webClient;
	}

	@WebRoute(path = "/{scheme}/{path:**}", method = Method.GET)
	public Publisher<ByteBuf> resolveDnsServiceAndGet(@PathParam String scheme, @PathParam String path, WebExchange<? extends ExchangeContext> exchange) {
		String query = exchange.request().getQuery();
		URI uri = URI.create(scheme + "://" + exchange.request().getAuthority() + "/" + path + (StringUtils.isNotBlank(query) ? "?" + query : ""));

		return this.webClient.exchange(uri)
			.flatMap(io.inverno.mod.web.client.WebExchange::response)
			.flatMapMany(response -> {
				if(response.headers().getStatusCode() >= 400) {
					return Mono.error(() -> HttpException.fromStatus(response.headers().getStatus()));
				}
				return response.body().raw().stream();
			});
	}
}
