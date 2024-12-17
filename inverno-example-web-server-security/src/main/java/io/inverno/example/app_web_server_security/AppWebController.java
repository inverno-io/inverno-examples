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
package io.inverno.example.app_web_server_security;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.http.base.ForbiddenException;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.security.accesscontrol.RoleBasedAccessController;
import io.inverno.mod.security.http.context.SecurityContext;
import io.inverno.mod.security.identity.Identity;
import io.inverno.mod.security.ldap.identity.LDAPIdentity;
import io.inverno.mod.web.server.annotation.WebController;
import io.inverno.mod.web.server.annotation.WebRoute;
import org.reactivestreams.Publisher;

/**
 * <p>
 * The application Web controller.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean
@WebController
public class AppWebController {
	
	@WebRoute(path = "/", produces = MediaTypes.TEXT_PLAIN)
	public String root() {
		return "Hello root";
	}
	
	@WebRoute(path = "/csrf", method = Method.POST, produces = MediaTypes.TEXT_PLAIN)
	public String csrf_post() {
		return "CSRF protected";
	}
	
	@WebRoute(path = "/hello", produces = MediaTypes.TEXT_PLAIN)
	public String public_hello(SecurityContext<? extends Identity, ? extends RoleBasedAccessController> securityContext) {
		return "Hello everyone";
	}

	@WebRoute(path = "/basic/hello", produces = MediaTypes.TEXT_PLAIN)
	public String basic_hello(SecurityContext<? extends Identity, ? extends RoleBasedAccessController> securityContext) {
		return "Hello basic";
	}

	@WebRoute(path = "/digest/hello", produces = MediaTypes.TEXT_PLAIN)
	public String digest_hello(SecurityContext<? extends Identity, ? extends RoleBasedAccessController> securityContext) {
		return "Hello digest";
	}

	@WebRoute(path = "/form/hello", produces = MediaTypes.TEXT_PLAIN)
	public String form_hello(SecurityContext<? extends Identity, ? extends RoleBasedAccessController> securityContext) {
		return "Hello form";
	}
	
	@WebRoute(path = "/ldap/hello", produces = MediaTypes.TEXT_PLAIN)
	public String ldap_hello(SecurityContext<? extends Identity, ? extends RoleBasedAccessController> securityContext) {
		return "Hello ldap";
	}
	
	@WebRoute(path = "/ldap/identity", produces = MediaTypes.APPLICATION_JSON)
	public LDAPIdentity ldap_identity(SecurityContext<? extends Identity, ? extends RoleBasedAccessController> securityContext) {
		return (LDAPIdentity)securityContext.getIdentity().orElse(null);
	}
	
	@WebRoute(path = "/ldap/read", produces = MediaTypes.TEXT_PLAIN)
	public Publisher<String> ldap_read(SecurityContext<? extends Identity, ? extends RoleBasedAccessController> securityContext) {
		return securityContext.getAccessController().get()
			.hasRole("readers").map(hasRole -> {
				if(!hasRole) {
					throw new ForbiddenException("User is not a reader");
				}
				return "User is a reader";
			});
	}
	
	@WebRoute(path = "/ldap/write", produces = MediaTypes.TEXT_PLAIN)
	public Publisher<String> ldap_write(SecurityContext<? extends Identity, ? extends RoleBasedAccessController> securityContext) {
		return securityContext.getAccessController().get()
			.hasRole("writers").map(hasRole -> {
				if(!hasRole) {
					throw new ForbiddenException("User is not a writer");
				}
				return "User is a writer";
			});
	}
}
