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
package io.inverno.example.app_web_server_security_session_jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Init;
import io.inverno.core.annotation.Wrapper;
import io.inverno.mod.base.concurrent.Reactor;
import io.inverno.mod.base.reflect.Types;
import io.inverno.mod.redis.RedisClient;
import io.inverno.mod.security.authentication.user.UserAuthentication;
import io.inverno.mod.security.identity.PersonIdentity;
import io.inverno.mod.security.jose.jwa.ECAlgorithm;
import io.inverno.mod.security.jose.jwa.ECCurve;
import io.inverno.mod.security.jose.jwa.OCTAlgorithm;
import io.inverno.mod.security.jose.jwk.JWK;
import io.inverno.mod.security.jose.jwk.JWKService;
import io.inverno.mod.security.jose.jwt.JWTService;
import io.inverno.mod.session.jwt.InMemoryJWTSessionStore;
import io.inverno.mod.session.jwt.JWTSessionIdGenerator;
import io.inverno.mod.session.jwt.JWTSessionStore;
import io.inverno.mod.session.jwt.RedisJWTSessionStore;
import java.util.UUID;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>
 * The session store bean exposing in-memory or Redis based stores with JWE or JWS session id generators based on {@link AppConfiguration}.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Wrapper @Bean
public class AppSessionStore implements Supplier<JWTSessionStore<AppSessionData, UserAuthentication<PersonIdentity>>> {

	private static final Logger LOGGER = LogManager.getLogger(AppSessionStore.class);

	private final AppConfiguration configuration;
	private final JWKService jwkService;
	private final JWTService jwtService;
	private final Reactor reactor;
	private final RedisClient<String, String> redisClient;
	private final ObjectMapper mapper;
	private final String keyId;

	public AppSessionStore(AppConfiguration configuration, JWKService jwkService, JWTService jwtService, Reactor reactor, RedisClient<String, String> redisClient, ObjectMapper mapper) {
		this.configuration = configuration;
		this.jwkService = jwkService;
		this.jwtService = jwtService;
		this.reactor = reactor;
		this.redisClient = redisClient;
		this.mapper = mapper;

		this.keyId = UUID.randomUUID().toString();
	}

	@Init
	public void init() {
		if(this.configuration.useJWE()) {
			this.jwkService.ec().generator()
				.keyId(this.keyId)
				.algorithm(ECAlgorithm.ECDH_ES.getAlgorithm())
				.curve(ECCurve.P_256.getCurve())
				.generate()
				.map(JWK::trust)
				.flatMap(jwkService.store()::set)
				.block();
		}
		else {
			this.jwkService.oct().generator()
				.keyId(this.keyId)
				.algorithm(OCTAlgorithm.HS256.getAlgorithm())
				.generate()
				.map(JWK::trust)
				.flatMap(jwkService.store()::set)
				.block();
		}
	}

	@Override
	public JWTSessionStore<AppSessionData, UserAuthentication<PersonIdentity>> get() {
		JWTSessionIdGenerator<AppSessionData, UserAuthentication<PersonIdentity>> sessionIdGenerator;
		if(this.configuration.useJWE()) {
			LOGGER.info("Using JWE session Id");
			sessionIdGenerator = JWTSessionIdGenerator.jwe(this.jwtService, header -> header.keyId(this.keyId).algorithm(ECAlgorithm.ECDH_ES.getAlgorithm()).encryptionAlgorithm(OCTAlgorithm.A256GCM.getAlgorithm()));
		}
		else {
			LOGGER.info("Using JWS session Id");
			sessionIdGenerator = JWTSessionIdGenerator.jws(this.jwtService, headers -> headers.keyId(this.keyId).algorithm(OCTAlgorithm.HS256.getAlgorithm()));
		}

		if(this.configuration.useRedisSessionStore()) {
			LOGGER.info("Using RedisSessionStore");
			return RedisJWTSessionStore.builder(
					sessionIdGenerator,
					this.redisClient,
					this.mapper,
					AppSessionData.class,
					Types.type(UserAuthentication.class).type(PersonIdentity.class).and().build()
				)
				.build();
		}
		else {
			LOGGER.info("Using InMemoryJWTSessionStore");
			return InMemoryJWTSessionStore.builder(
					sessionIdGenerator,
					this.reactor,
					this.mapper,
					Types.type(UserAuthentication.class).type(PersonIdentity.class).and().build()
				)
				.build();
		}
	}
}
