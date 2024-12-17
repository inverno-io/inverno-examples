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
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.base.UnauthorizedException;
import io.inverno.mod.security.accesscontrol.RoleBasedAccessController;
import io.inverno.mod.security.authentication.CredentialsResolver;
import io.inverno.mod.security.authentication.LoginCredentials;
import io.inverno.mod.security.authentication.PrincipalAuthenticator;
import io.inverno.mod.security.http.AccessControlInterceptor;
import io.inverno.mod.security.http.SecurityInterceptor;
import io.inverno.mod.security.http.context.InterceptingSecurityContext;
import io.inverno.mod.security.http.digest.DigestAuthenticationErrorInterceptor;
import io.inverno.mod.security.http.digest.DigestCredentialsExtractor;
import io.inverno.mod.security.http.digest.DigestCredentialsMatcher;
import io.inverno.mod.security.identity.Identity;
import io.inverno.mod.web.server.ErrorWebRouteInterceptor;
import io.inverno.mod.web.server.WebRouteInterceptor;
import java.util.List;

/**
 * <p>
 * This configurer configures the Web router to secure {@code /digest/**} routes using Digest authentication (RFC7616 - HTTP Digest Access Authentication).
 * </p>
 *
 * <p>
 * The {@link SecurityInterceptor} is set on {@code /digest/**} routes using the {@link DigestCredentialsExtractor} to extract credentials and a {@link PrincipalAuthenticator} with a
 * {@link DigestCredentialsMatcher} to authenticate users. The {@link DigestAuthenticationErrorInterceptor} is defined on {@code /digest/**} error routes to intercept {@link UnauthorizedException}
 * ({@code 401}) and specifies the {@code www-authenticate} header with Digest authentication scheme.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean( visibility = Bean.Visibility.PRIVATE )
public class DigestRouterConfigurer implements WebRouteInterceptor.Configurer<InterceptingSecurityContext<Identity, RoleBasedAccessController>>, ErrorWebRouteInterceptor.Configurer<ExchangeContext> {

	private final CredentialsResolver<? extends LoginCredentials> credentialsResolver;
	
	public DigestRouterConfigurer(CredentialsResolver<? extends LoginCredentials> credentialsResolver) {
		this.credentialsResolver = credentialsResolver;
	}
	
	@Override
	public WebRouteInterceptor<InterceptingSecurityContext<Identity, RoleBasedAccessController>> configure(WebRouteInterceptor<InterceptingSecurityContext<Identity, RoleBasedAccessController>> interceptors) {
		return interceptors
			.intercept()
				.path("/digest/**")
				.interceptors(List.of(
					SecurityInterceptor.of(
						new DigestCredentialsExtractor(),
						new PrincipalAuthenticator<>(this.credentialsResolver, new DigestCredentialsMatcher<>("secret"))
					),
					AccessControlInterceptor.authenticated()
				));
	}

	@Override
	public ErrorWebRouteInterceptor<ExchangeContext> configure(ErrorWebRouteInterceptor<ExchangeContext> errorInterceptors) {
		return errorInterceptors
			.interceptError()
				.error(UnauthorizedException.class)
				.path("/digest/**")
				.interceptor(new DigestAuthenticationErrorInterceptor<>("inverno-digest", "secret"));
	}
}
