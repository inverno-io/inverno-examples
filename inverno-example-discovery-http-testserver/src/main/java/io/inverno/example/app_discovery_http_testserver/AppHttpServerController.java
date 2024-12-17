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
package io.inverno.example.app_discovery_http_testserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.base.HttpException;
import io.inverno.mod.http.base.InternalServerErrorException;
import io.inverno.mod.http.server.ErrorExchange;
import io.inverno.mod.http.server.Exchange;
import io.inverno.mod.http.server.ServerController;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * The HTTP server controller responding with a {@link TestServerResponse} to any incoming request.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean
public class AppHttpServerController implements ServerController<ExchangeContext, Exchange<ExchangeContext>, ErrorExchange<ExchangeContext>> {

	private final ObjectMapper mapper;

	public AppHttpServerController(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void handle(Exchange<ExchangeContext> exchange) throws HttpException {
		TestServerResponse response = new TestServerResponse(
			exchange.request().getLocalAddress().toString(),
			exchange.request().getRemoteAddress().toString(),
			exchange.request().getMethod().name(),
			exchange.request().getPath(),
			exchange.request().getAuthority(),
			exchange.request().headers().getAll().stream().collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.joining(", "))))
		);

		try {
			exchange.response()
				.headers(headers -> headers.contentType(MediaTypes.APPLICATION_JSON))
				.body().string().value(this.mapper.writeValueAsString(response));
		}
		catch (JsonProcessingException e) {
			throw new InternalServerErrorException(e);
		}
	}
}
