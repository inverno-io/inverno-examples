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
import io.inverno.mod.http.server.ExchangeInterceptor;
import io.inverno.mod.security.accesscontrol.RoleBasedAccessController;
import io.inverno.mod.security.authentication.CredentialsResolver;
import io.inverno.mod.security.authentication.LoginCredentials;
import io.inverno.mod.security.authentication.LoginCredentialsMatcher;
import io.inverno.mod.security.authentication.PrincipalAuthentication;
import io.inverno.mod.security.authentication.PrincipalAuthenticator;
import io.inverno.mod.security.http.AccessControlInterceptor;
import io.inverno.mod.security.http.SecurityInterceptor;
import io.inverno.mod.security.http.basic.BasicAuthenticationErrorInterceptor;
import io.inverno.mod.security.http.basic.BasicCredentialsExtractor;
import io.inverno.mod.security.http.context.InterceptingSecurityContext;
import io.inverno.mod.security.identity.Identity;
import io.inverno.mod.web.server.ErrorWebRouteInterceptor;
import io.inverno.mod.web.server.WebExchange;
import io.inverno.mod.web.server.WebRouteInterceptor;
import java.util.List;

/**
 * <p>
 * This configurer configures the Web server to secure {@code /basic/**} routes using Basic authentication (RFC7617 - The 'Basic' HTTP Authentication Scheme).
 * </p>
 *
 * <p>
 * The {@link SecurityInterceptor} is set on {@code /basic/**} routes using the {@link BasicCredentialsExtractor} to extract credentials and a simple {@link PrincipalAuthenticator} with a
 * {@link LoginCredentialsMatcher} to authenticate users. The {@link BasicAuthenticationErrorInterceptor} is defined on {@code /basic/**} error routes to intercept {@link UnauthorizedException}
 * ({@code 401}) and specifies the {@code www-authenticate} header with Basic authentication scheme.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean( visibility = Bean.Visibility.PRIVATE )
public class BasicRouterConfigurer implements WebRouteInterceptor.Configurer<InterceptingSecurityContext<Identity, RoleBasedAccessController>>, ErrorWebRouteInterceptor.Configurer<ExchangeContext> {

   private final CredentialsResolver<? extends LoginCredentials> credentialsResolver;

	public BasicRouterConfigurer(CredentialsResolver<? extends LoginCredentials> credentialsResolver) {
		this.credentialsResolver = credentialsResolver;
	}

	@Override
	public WebRouteInterceptor<InterceptingSecurityContext<Identity, RoleBasedAccessController>> configure(WebRouteInterceptor<InterceptingSecurityContext<Identity, RoleBasedAccessController>> interceptors) {
		// Interceptors can be safely composed using ExchangeInterceptor.of()
		return interceptors
			.intercept()
			.path("/basic/**")
			.interceptors(List.of(
				SecurityInterceptor.of(
					new BasicCredentialsExtractor(),
					new PrincipalAuthenticator<>(this.credentialsResolver, new LoginCredentialsMatcher<>())
				),
				AccessControlInterceptor.authenticated()
			));

		// In order to chain multiple interceptors using compose() or andThen(), we must make sure the Exchange type is a WebExchange, since SecurityInterceptor and AccessControlInterceptor uses parameterized Exchange type, we must then be explicit:
		/*return interceptors
			.intercept()
				.path("/basic/**")
				.interceptor(
					SecurityInterceptor.<LoginCredentials, PrincipalAuthentication, Identity, RoleBasedAccessController, InterceptingSecurityContext<Identity, RoleBasedAccessController>, WebExchange<InterceptingSecurityContext<Identity, RoleBasedAccessController>>>of(
						new BasicCredentialsExtractor(),
						new PrincipalAuthenticator<>(this.credentialsResolver, new LoginCredentialsMatcher<>())
					).andThen(AccessControlInterceptor.authenticated())
				);*/

		/*interceptors
			.intercept()
				.path("/basic/**")
				.interceptor(ExchangeInterceptor.of(
					SecurityInterceptor.<LoginCredentials, PrincipalAuthentication, Identity, RoleBasedAccessController, InterceptingSecurityContext<Identity, RoleBasedAccessController>, WebExchange<InterceptingSecurityContext<Identity, RoleBasedAccessController>>>of(
						new BasicCredentialsExtractor(),
						new PrincipalAuthenticator<>(this.credentialsResolver, new LoginCredentialsMatcher<>())
					),
					AccessControlInterceptor.authenticated()
				));*/
	}

	@Override
	public ErrorWebRouteInterceptor<ExchangeContext> configure(ErrorWebRouteInterceptor<ExchangeContext> errorInterceptors) {
		return errorInterceptors
			.interceptError()
				.error(UnauthorizedException.class)
				.path("/basic/**")
				.interceptor(new BasicAuthenticationErrorInterceptor<>("inverno-basic"));
	}
}
