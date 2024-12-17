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
import io.inverno.mod.security.accesscontrol.RoleBasedAccessController;
import io.inverno.mod.security.context.SecurityContext;
import io.inverno.mod.security.identity.PersonIdentity;
import reactor.core.publisher.Mono;

/**
 * <p>
 * A simple module bean.
 * <p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean
public class HelloService {
	
	private final SecurityContext<PersonIdentity, RoleBasedAccessController> securityContext;
	
    public HelloService(SecurityContext<PersonIdentity, RoleBasedAccessController> securityContext) {
		this.securityContext = securityContext;
	}
    
    public void sayHello() {
		this.securityContext.getAccessController()
			.map(rbac -> rbac.hasRole("vip"))
			.orElse(Mono.just(false))
			.subscribe(isVip -> {
				StringBuilder message = new StringBuilder();
				if(isVip) {
					message.append("Hello my dear friend ").append(this.securityContext.getIdentity().map(PersonIdentity::getFirstName).orElse("whoever you are")).append("!");
				}
				else {
					message.append("Hello ").append(this.securityContext.getIdentity().map(PersonIdentity::getFirstName).orElse("whoever you are")).append("!");
				}
				System.out.println(message.toString());
			});
    }
}
