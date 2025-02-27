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

import io.inverno.core.annotation.NestedBean;
import io.inverno.mod.boot.BootConfiguration;
import io.inverno.mod.configuration.Configuration;
import io.inverno.mod.web.server.WebServerConfiguration;

/**
 * <p>
 * The application configuration providing the Boot and Web server configurations and flags indicating whether Redis should be used as session store or whether the JWT claims set should be wrapped in
 * a JWE instead of a JWS.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Configuration
public interface AppConfiguration {

	@NestedBean
	BootConfiguration boot();

	@NestedBean
	WebServerConfiguration web();

	default boolean useRedisSessionStore() {
		return false;
	}

	default boolean useJWE() {
		return false;
	}
}
