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
package io.inverno.example.app_web_security;

import io.inverno.core.annotation.NestedBean;
import io.inverno.mod.boot.BootConfiguration;
import io.inverno.mod.configuration.Configuration;
import io.inverno.mod.ldap.LDAPClientConfiguration;
import java.net.URI;

/**
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Configuration
public interface AppConfiguration {
	
	@NestedBean
	BootConfiguration boot();
	
	@NestedBean
	default LDAPClientConfiguration ldap_client() {
		return new LDAPClientConfiguration() {
			@Override
			public URI uri() {
				return URI.create("ldap://127.0.0.1:1389");
			}

			@Override
			public String admin_dn() {
				return null;
			}

			@Override
			public String admin_credentials() {
				return null;
			}
		};
	}
}
