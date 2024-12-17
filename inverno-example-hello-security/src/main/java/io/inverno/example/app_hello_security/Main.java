/*
 * Copyright 2021 Jeremy KUHN
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
package io.inverno.example.app_hello_security;

import io.inverno.core.annotation.Bean;
import io.inverno.core.v1.Application;
import io.inverno.mod.security.SecurityManager;
import io.inverno.mod.security.accesscontrol.GroupsRoleBasedAccessControllerResolver;
import io.inverno.mod.security.accesscontrol.RoleBasedAccessController;
import io.inverno.mod.security.authentication.LoginCredentials;
import io.inverno.mod.security.authentication.LoginCredentialsMatcher;
import io.inverno.mod.security.authentication.password.RawPassword;
import io.inverno.mod.security.authentication.user.InMemoryUserRepository;
import io.inverno.mod.security.authentication.user.User;
import io.inverno.mod.security.authentication.user.UserAuthenticator;
import io.inverno.mod.security.context.SecurityContext;
import io.inverno.mod.security.identity.PersonIdentity;
import io.inverno.mod.security.identity.UserIdentityResolver;
import java.util.List;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>
 * Starts an Inverno module and invoke a method on a public bean.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
public class Main {
	
	private static final Logger LOGGER = LogManager.getLogger(Main.class);
	
	@Bean
	public static interface App_hello_securitySecurityContext extends Supplier<SecurityContext<PersonIdentity, RoleBasedAccessController>> {}
	
    public static void main(String[] args) {
		if(args.length != 2) {
    		System.out.println("Usage: hello <user> <password>");
    		return;
    	}
		
		// The security manager which uses a simple user authenticator with an in-memory user repository and a Login credentials (i.e. login/pasword) matcher
		SecurityManager<LoginCredentials, PersonIdentity, RoleBasedAccessController> securityManager = SecurityManager.of(
			new UserAuthenticator<>(
				InMemoryUserRepository
					.of(List.of(
						User.of("jsmith")
							.password(new RawPassword("password"))
							.identity(new PersonIdentity("jsmith", "John", "Smith", "jsmith@inverno.io"))
							.groups("vip")
							.build(),
						User.of("adoe")
							.password(new RawPassword("password"))
							.identity(new PersonIdentity("adoe", "Alice", "Doe", "adoe@inverno.io"))
							.build()
					))
					.build(),
				new LoginCredentialsMatcher<>()
			),
			new UserIdentityResolver<>(),
			new GroupsRoleBasedAccessControllerResolver()
		);
		
		securityManager.authenticate(LoginCredentials.of(args[0], new RawPassword(args[1])))
			.subscribe(securityContext -> {
				if(securityContext.isAuthenticated()) {
					LOGGER.info("User has been authenticated");
					Application.run(new App_hello_security.Builder(securityContext)).helloService().sayHello();
				}
				else {
					securityContext.getAuthentication().getCause().ifPresentOrElse(
						error -> LOGGER.error("Failed to authenticate user", error),
						() -> LOGGER.error("Unauthorized anonymous access")
					);
				}
			});
    }
}
