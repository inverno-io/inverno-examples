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
package io.inverno.example.app_web_server_session;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Wrapper;
import io.inverno.mod.base.concurrent.Reactor;
import io.inverno.mod.redis.RedisClient;
import io.inverno.mod.session.BasicSessionStore;
import io.inverno.mod.session.InMemoryBasicSessionStore;
import io.inverno.mod.session.RedisBasicSessionStore;
import io.inverno.mod.session.SessionIdGenerator;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>
 * The session store bean exposing in-memory or Redis based stores based on {@link AppConfiguration}.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Wrapper @Bean
public class AppSessionStore implements Supplier<BasicSessionStore<AppSessionData>> {

	private static final Logger LOGGER = LogManager.getLogger(AppSessionStore.class);

	private final AppConfiguration configuration;
	private final Reactor reactor;
	private final RedisClient<String, String> redisClient;
	private final ObjectMapper mapper;

	public AppSessionStore(AppConfiguration configuration, Reactor reactor, RedisClient<String, String> redisClient, ObjectMapper mapper) {
		this.configuration = configuration;
		this.reactor = reactor;
		this.redisClient = redisClient;
		this.mapper = mapper;
	}

	@Override
	public BasicSessionStore<AppSessionData> get() {
		if(this.configuration.useRedisSessionStore()) {
			LOGGER.info("Using RedisSessionStore");
			return RedisBasicSessionStore.builder(this.redisClient, this.mapper, AppSessionData.class).build();
		}
		else {
			LOGGER.info("Using InMemorySessionStore");
			return InMemoryBasicSessionStore.<AppSessionData>builder(this.reactor).build();
		}
	}
}
