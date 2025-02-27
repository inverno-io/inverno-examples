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

import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.security.accesscontrol.RoleBasedAccessController;
import io.inverno.mod.security.http.context.SecurityContext;
import io.inverno.mod.security.identity.PersonIdentity;
import io.inverno.mod.session.Session;
import io.inverno.mod.session.http.context.SessionContext;
import io.inverno.mod.web.server.WebExchange;
import io.inverno.mod.web.server.annotation.WebController;
import io.inverno.mod.web.server.annotation.WebRoute;
import reactor.core.publisher.Mono;

/**
 * <p>
 * The application Web controller.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean @WebController( path = "/api" )
public class AppWebController {

	@WebRoute( path = "user", method = Method.GET, produces = MediaTypes.APPLICATION_JSON )
	public Mono<PersonIdentity> getUserIdentity(SecurityContext<PersonIdentity, RoleBasedAccessController> securityContext) {
		return securityContext.getIdentity().map(Mono::just).orElse(Mono.empty());
	}

	@WebRoute( path = "counter", method = Method.GET, produces = MediaTypes.TEXT_PLAIN )
	public Mono<Integer> getCounter(SessionContext<AppSessionData, ? extends Session<AppSessionData>> sessionContext) {
		return sessionContext.getSessionData(AppSessionData::new)
			.map(AppSessionData::getCounter);
	}

	@WebRoute( path = "counter", method = Method.POST, produces = MediaTypes.TEXT_PLAIN )
	public Mono<Integer> incrementCounter(SessionContext<AppSessionData, ? extends Session<AppSessionData>> sessionContext) {
		return sessionContext.getSessionData(AppSessionData::new)
			.map(AppSessionData::incrementAndGetCounter);
	}
}
