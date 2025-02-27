/*
 * Copyright 2025 Jeremy KUHN
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
package io.inverno.example.app_web_server_session_jwt;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.http.base.BadRequestException;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.Parameter;
import io.inverno.mod.session.Session;
import io.inverno.mod.session.http.context.jwt.JWTSessionContext;
import io.inverno.mod.web.server.WebExchange;
import io.inverno.mod.web.server.annotation.WebController;
import io.inverno.mod.web.server.annotation.WebRoute;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import reactor.core.publisher.Mono;

/**
 * <p>
 * The session REST controller.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean
@WebController( path = "/session" )
public class AppSessionController {

	@WebRoute( method = Method.GET, produces = MediaTypes.APPLICATION_JSON )
	public Mono<Map<String, Object>> getSession(JWTSessionContext<AppSessionData, Map<String, String>> sessionContext) {
		return Mono.defer(() -> {
			if(!sessionContext.isSessionPresent()) {
				return Mono.just(Map.of());
			}
			return sessionContext.getSession()
				.flatMap(session ->  session.getData(AppSessionData::new)
					.map(sessionData -> {
						Map<String, Object> sessionMap = new LinkedHashMap<>();

						sessionMap.put("id", session.getId());
						sessionMap.put("creationTime", session.getCreationTime());
						sessionMap.put("lastAccessedTime", session.getLastAccessedTime());
						if(session.getMaxInactiveInterval() != null) {
							sessionMap.put("maxInactiveInterval", session.getMaxInactiveInterval());
						}
						sessionMap.put("expirationTime", session.getExpirationTime());
						sessionMap.put("new", session.isNew());
						sessionMap.put("statelessData", session.getStatelessData());
						sessionMap.put("data", sessionData);

						return sessionMap;
					})
				);
		});
	}

	@WebRoute( path = "increment-counter", method = Method.POST, produces = MediaTypes.APPLICATION_JSON )
	public Mono<Map<String, Object>> incrementSessionCounter(JWTSessionContext<AppSessionData, Map<String, String>> sessionContext) {
		return sessionContext.getSessionData(AppSessionData::new).map(AppSessionData::incrementAndGetCounter).then(this.getSession(sessionContext));
	}

	@WebRoute( path = "set-stateless-data", method = Method.POST, consumes = MediaTypes.APPLICATION_X_WWW_FORM_URLENCODED, produces = MediaTypes.APPLICATION_JSON )
	public Mono<Map<String, Object>> setStatelessData(WebExchange<? extends JWTSessionContext<AppSessionData, Map<String, String>>> exchange) {
		if(exchange.context().isSessionPresent()) {
			exchange.response().body()
				.before(exchange.context().getSession()
					.flatMap(session -> exchange.request().body()
						.map(body -> body.urlEncoded().collectMap()
							.doOnSuccess(parameters -> {
								if(!parameters.containsKey("key")) {
									throw new BadRequestException("Missing parameter key");
								}
								Map<String, String> statelessData = session.getStatelessData(HashMap::new);
								if(parameters.containsKey("value")) {
									statelessData.put(
										parameters.get("key").asString(),
										parameters.get("value").asString()
									);
								}
								else {
									statelessData.remove(parameters.get("key").asString());
								}
								// By default, stateless data are only saved on set which is the recommended behaviour so we have to invoke setStatelessData explicitly to trigger a refresh id
								// This can be fine-tuned using a SessionDataSaveStrategy
								session.setStatelessData(statelessData);
							})
							.then()
						)
						.orElse(Mono.empty())
					)
				);
		}
		return this.getSession(exchange.context());
	}

	@WebRoute( path = "max-inactive-interval", method = Method.POST, consumes = MediaTypes.APPLICATION_X_WWW_FORM_URLENCODED, produces = MediaTypes.APPLICATION_JSON )
	public Mono<Map<String, Object>> setMaxInactiveInterval(WebExchange<? extends JWTSessionContext<AppSessionData, Map<String, String>>> exchange) {
		if(exchange.context().isSessionPresent()) {
			exchange.response().body()
				.before(exchange.context().getSession()
					.flatMap(session -> exchange.request().body()
						.map(body -> body.urlEncoded().collectMap()
							.doOnSuccess(parameters -> {
								Parameter maxInactiveInterval = parameters.get("maxInactiveInterval");
								if(maxInactiveInterval != null) {
									session.setMaxInactiveInterval(maxInactiveInterval.asLong());
								}
							})
							.then()
						)
						.orElse(Mono.empty())
					)
				);
		}
		return this.getSession(exchange.context());
	}

	@WebRoute( path = "expiration-time", method = Method.POST, consumes = MediaTypes.APPLICATION_X_WWW_FORM_URLENCODED, produces = MediaTypes.APPLICATION_JSON )
	public Mono<Map<String, Object>> setExpirationTime(WebExchange<? extends JWTSessionContext<AppSessionData, Map<String, String>>> exchange) {
		if(exchange.context().isSessionPresent()) {
			exchange.response().body()
				.before(exchange.context().getSession()
					.flatMap(session -> exchange.request().body()
						.map(body -> body.urlEncoded().collectMap()
							.doOnSuccess(parameters -> {
								Parameter expirationTime = parameters.get("expirationTime");
								if(expirationTime != null) {
									session.setExpirationTime(expirationTime.asLong());
								}
							})
							.then()
						)
						.orElse(Mono.empty())
					)
				);
		}
		return this.getSession(exchange.context());
	}

	@WebRoute( path = "/refresh-id", method = Method.POST, produces = MediaTypes.APPLICATION_JSON )
	public Mono<Map<String, Object>> refreshSessionId(WebExchange<? extends JWTSessionContext<AppSessionData, Map<String, String>>> exchange) {
		if(exchange.context().isSessionPresent()) {
			exchange.response().body()
				.before(exchange.context().getSession().flatMap(session -> session.refreshId(true)).then());
		}
		return this.getSession(exchange.context());
	}

	@WebRoute( path = "/invalidate", method = Method.POST )
	public void invalidateSession(WebExchange<? extends JWTSessionContext<AppSessionData, Map<String, String>>> exchange) {
		exchange.response().body()
			.before(exchange.context().getSession().flatMap(Session::invalidate));
	}
}
