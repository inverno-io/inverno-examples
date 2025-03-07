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

/**
 * <p>
 * Inverno example application module demonstrating how to secure a Web application by storing authentication in a JWT session.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@io.inverno.core.annotation.Module
module io.inverno.example.app_web_server_security_session_jwt {
	requires io.inverno.mod.boot;
	requires io.inverno.mod.configuration;
	requires io.inverno.mod.web.server;
	requires io.inverno.mod.redis.lettuce;
	requires io.inverno.mod.security.http;
	requires io.inverno.mod.security.jose;
	requires io.inverno.mod.session.http;

	requires com.fasterxml.jackson.databind;
	requires org.apache.logging.log4j;

	opens io.inverno.example.app_web_server_security_session_jwt to com.fasterxml.jackson.databind;
}
