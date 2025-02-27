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
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.base.resource.ModuleResource;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.UnauthorizedException;
import io.inverno.mod.security.accesscontrol.GroupsRoleBasedAccessControllerResolver;
import io.inverno.mod.security.accesscontrol.RoleBasedAccessController;
import io.inverno.mod.security.authentication.CredentialsResolver;
import io.inverno.mod.security.authentication.LoginCredentialsMatcher;
import io.inverno.mod.security.authentication.user.User;
import io.inverno.mod.security.authentication.user.UserAuthentication;
import io.inverno.mod.security.authentication.user.UserAuthenticator;
import io.inverno.mod.security.http.AccessControlInterceptor;
import io.inverno.mod.security.http.SecurityInterceptor;
import io.inverno.mod.security.http.form.FormAuthenticationErrorInterceptor;
import io.inverno.mod.security.http.form.FormCredentialsExtractor;
import io.inverno.mod.security.http.form.FormLoginPageHandler;
import io.inverno.mod.security.http.form.RedirectLoginFailureHandler;
import io.inverno.mod.security.http.form.RedirectLoginSuccessHandler;
import io.inverno.mod.security.http.form.RedirectLogoutSuccessHandler;
import io.inverno.mod.security.http.login.LoginActionHandler;
import io.inverno.mod.security.http.login.LoginSuccessHandler;
import io.inverno.mod.security.http.login.LogoutActionHandler;
import io.inverno.mod.security.http.login.LogoutSuccessHandler;
import io.inverno.mod.security.http.session.BasicSessionCredentialsExtractor;
import io.inverno.mod.security.http.session.BasicSessionLoginSuccessHandler;
import io.inverno.mod.security.http.session.BasicSessionSecurityContext;
import io.inverno.mod.security.http.session.SessionAuthenticator;
import io.inverno.mod.security.http.session.SessionLogoutSuccessHandler;
import io.inverno.mod.security.identity.PersonIdentity;
import io.inverno.mod.security.identity.UserIdentityResolver;
import io.inverno.mod.session.BasicSessionStore;
import io.inverno.mod.session.http.CookieSessionIdExtractor;
import io.inverno.mod.session.http.CookieSessionInjector;
import io.inverno.mod.session.http.SessionInterceptor;
import io.inverno.mod.web.server.WebExchange;
import io.inverno.mod.web.server.WebServer;
import io.inverno.mod.web.server.WhiteLabelErrorRoutesConfigurer;
import java.net.URI;
import java.util.List;
import reactor.core.publisher.Mono;

/**
 * <p>
 * The Web server configurer configuring security interceptors and routes with basic session support.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean( visibility = Bean.Visibility.PRIVATE )
public class AppWebServerConfigurer implements WebServer.Configurer<BasicSessionSecurityContext.Intercepted<UserAuthentication<PersonIdentity>, PersonIdentity, RoleBasedAccessController, AppAuthSessionData>> {

	private final BasicSessionStore<AppAuthSessionData> sessionStore;
	private final CredentialsResolver<? extends User<PersonIdentity>> credentialsResolver;

	public AppWebServerConfigurer(BasicSessionStore<AppAuthSessionData> sessionStore, CredentialsResolver<? extends User<PersonIdentity>> credentialsResolver) {
		this.sessionStore = sessionStore;
		this.credentialsResolver = credentialsResolver;
	}

	@Override
	public WebServer<BasicSessionSecurityContext.Intercepted<UserAuthentication<PersonIdentity>, PersonIdentity, RoleBasedAccessController, AppAuthSessionData>> configure(WebServer<BasicSessionSecurityContext.Intercepted<UserAuthentication<PersonIdentity>, PersonIdentity, RoleBasedAccessController, AppAuthSessionData>> webServer) {
		return webServer
			.interceptError()
				.error(UnauthorizedException.class)
				.interceptor(new FormAuthenticationErrorInterceptor<>())
			.configureErrorRoutes(new WhiteLabelErrorRoutesConfigurer<>())
			.intercept()
				.interceptor(SessionInterceptor.of(
					new CookieSessionIdExtractor<>(),
					this.sessionStore,
					new CookieSessionInjector<>()
				))
			.route()
				.method(Method.GET)
				.path("/login")
				.produce(MediaTypes.TEXT_HTML)
				.handler(new FormLoginPageHandler<>())
			.route()
				.method(Method.POST)
				.path("/login")
				.handler(new LoginActionHandler<>(
					new FormCredentialsExtractor<>(),
					new UserAuthenticator<>(this.credentialsResolver, new LoginCredentialsMatcher<>()).failOnDenied(),
					LoginSuccessHandler.<UserAuthentication<PersonIdentity>, BasicSessionSecurityContext.Intercepted<UserAuthentication<PersonIdentity>, PersonIdentity, RoleBasedAccessController, AppAuthSessionData>, WebExchange<BasicSessionSecurityContext.Intercepted<UserAuthentication<PersonIdentity>, PersonIdentity, RoleBasedAccessController, AppAuthSessionData>>>of(
						new BasicSessionLoginSuccessHandler<>(AppAuthSessionData::new),
						new RedirectLoginSuccessHandler<>()
					),
					new RedirectLoginFailureHandler<>()
				))
			.intercept()
				.interceptors(List.of(
					SecurityInterceptor.of(
						new BasicSessionCredentialsExtractor<UserAuthentication<PersonIdentity>, AppAuthSessionData, BasicSessionSecurityContext.Intercepted<UserAuthentication<PersonIdentity>, PersonIdentity, RoleBasedAccessController, AppAuthSessionData>, WebExchange<BasicSessionSecurityContext.Intercepted<UserAuthentication<PersonIdentity>, PersonIdentity, RoleBasedAccessController, AppAuthSessionData>>>(),
						new SessionAuthenticator<>(),
						new UserIdentityResolver<>(),
						new GroupsRoleBasedAccessControllerResolver()
					),
					AccessControlInterceptor.authenticated()
				))
			.route()
				.method(Method.GET)
				.handler(exchange -> exchange.response().body().resource().value(new ModuleResource(URI.create("module://io.inverno.example.app_web_server_security_session/index.html"))))
			.route()
				.method(Method.GET)
				.path("/logout")
				.handler(new LogoutActionHandler<>(
					authentication -> Mono.empty(), // release the authentication: free resources, return a token to a pool...
					LogoutSuccessHandler.<UserAuthentication<PersonIdentity>, PersonIdentity, RoleBasedAccessController, BasicSessionSecurityContext.Intercepted<UserAuthentication<PersonIdentity>, PersonIdentity, RoleBasedAccessController, AppAuthSessionData>, WebExchange<BasicSessionSecurityContext.Intercepted<UserAuthentication<PersonIdentity>, PersonIdentity, RoleBasedAccessController, AppAuthSessionData>>>of(
						new SessionLogoutSuccessHandler<>(),
						new RedirectLogoutSuccessHandler<>()
					)
				));
	}
}
