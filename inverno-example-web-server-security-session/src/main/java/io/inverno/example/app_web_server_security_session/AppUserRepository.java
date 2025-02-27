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
package io.inverno.example.app_web_server_security_session;

import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Wrapper;
import io.inverno.mod.security.authentication.password.RawPassword;
import io.inverno.mod.security.authentication.user.InMemoryUserRepository;
import io.inverno.mod.security.authentication.user.User;
import io.inverno.mod.security.authentication.user.UserRepository;
import io.inverno.mod.security.identity.PersonIdentity;
import java.util.List;
import java.util.function.Supplier;

/**
 * <p>
 * The application user repository specifying the application users credentials and used for authentication.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Wrapper @Bean
public class AppUserRepository implements Supplier<UserRepository<PersonIdentity, User<PersonIdentity>>> {

	@Override
	public UserRepository<PersonIdentity, User<PersonIdentity>> get() {
		return InMemoryUserRepository
			.of(List.of(
				User.of("jsmith")
					.identity(new PersonIdentity("jsmith", "John", "Smith", "jsmith@inverno.io"))
					.password(new RawPassword("password"))
					.groups("group1", "group2")
					.build()
			))
			.passwordEncoder(new RawPassword.Encoder())
			.build();
	}
}
