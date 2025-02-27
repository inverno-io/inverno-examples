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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.inverno.mod.security.authentication.user.UserAuthentication;
import io.inverno.mod.security.http.session.AuthSessionData;
import io.inverno.mod.security.identity.PersonIdentity;

/**
 * <p>
 * Session data with authentication data.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppAuthSessionData extends AppSessionData implements AuthSessionData<UserAuthentication<PersonIdentity>> {

	private UserAuthentication<PersonIdentity> authentication;

	public AppAuthSessionData() {
		super(0);
	}

	@JsonCreator
	public AppAuthSessionData(@JsonProperty("counter") int counter) {
		super(counter);
	}

	@Override
	public UserAuthentication<PersonIdentity> getAuthentication() {
		return this.authentication;
	}

	@Override
	public void setAuthentication(UserAuthentication<PersonIdentity> authentication) {
		this.authentication = authentication;
	}
}
