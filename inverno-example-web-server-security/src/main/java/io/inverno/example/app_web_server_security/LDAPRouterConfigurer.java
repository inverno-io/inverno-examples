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
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.UnauthorizedException;
import io.inverno.mod.ldap.LDAPClient;
import io.inverno.mod.security.accesscontrol.GroupsRoleBasedAccessControllerResolver;
import io.inverno.mod.security.accesscontrol.RoleBasedAccessController;
import io.inverno.mod.security.http.AccessControlInterceptor;
import io.inverno.mod.security.http.SecurityInterceptor;
import io.inverno.mod.security.http.context.SecurityContext;
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
import io.inverno.mod.security.http.token.BearerTokenCredentialsExtractor;
import io.inverno.mod.security.http.token.CookieTokenCredentialsExtractor;
import io.inverno.mod.security.http.token.CookieTokenLoginSuccessHandler;
import io.inverno.mod.security.http.token.CookieTokenLogoutSuccessHandler;
import io.inverno.mod.security.identity.Identity;
import io.inverno.mod.security.jose.jwa.OCTAlgorithm;
import io.inverno.mod.security.jose.jwk.JWKService;
import io.inverno.mod.security.jose.jwk.oct.OCTJWK;
import io.inverno.mod.security.jose.jws.JWSAuthentication;
import io.inverno.mod.security.jose.jws.JWSAuthenticator;
import io.inverno.mod.security.jose.jws.JWSService;
import io.inverno.mod.security.jose.jwt.JWTSAuthentication;
import io.inverno.mod.security.ldap.authentication.LDAPAuthentication;
import io.inverno.mod.security.ldap.authentication.LDAPAuthenticator;
import io.inverno.mod.security.ldap.identity.LDAPIdentityResolver;
import io.inverno.mod.web.server.ErrorWebRouteInterceptor;
import io.inverno.mod.web.server.WebExchange;
import io.inverno.mod.web.server.WebRouteInterceptor;
import io.inverno.mod.web.server.WebRouter;
import io.inverno.mod.web.server.annotation.WebRoute;
import io.inverno.mod.web.server.annotation.WebRoutes;
import java.util.List;
import reactor.core.publisher.Mono;

/**
 * <p>
 * This configurer configures the Web router to secure {@code /ldap/**} routes using Form based authentication and authenticating users on an LDAP server.
 * </p>
 * 
 * <p>
 * The {@link SecurityInterceptor} is set on {@code /ldap/**} routes using {@link BearerTokenCredentialsExtractor} or {@link CookieTokenCredentialsExtractor} to extract token credentials.
 * Authentication is done by verifying the token as a JSON Web Signature whose payload is an {@link LDAPAuthentication}. An {@link LDAPIdentityResolver} is used to obtain the identify of the
 * authenticated user and a {@link GroupsRoleBasedAccessControllerResolver} is used to provide a {@link RoleBasedAccessController} based on the groups the authenticated user belongs to. The
 * {@link FormAuthenticationErrorInterceptor} is defined on {@code /ldap/**} error routes to intercept {@link UnauthorizedException} ({@code 401}) and redirect the client to the form login page
 * ({@code /login/ldap}).
 * </p>
 * 
 * <p>
 * The configurer defines three routes:
 * </p>
 * 
 * <ul>
 * <li>{@code GET /login/ldap} which displays the white label login page using the {@link FormLoginPageHandler}.</li>
 * <li>{@code POST /login/ldap} which signs the user in using a {@link LoginActionHandler} with {@link FormCredentialsExtractor} to extract user credentials and an {@link LDAPAuthenticator} to
 * authenticate the user. The resulting {@link LDAPAuthentication} is then transformed into a {@link JWSAuthentication}. The {@link CookieTokenLoginSuccessHandler} sets the resulting token in a
 * cookie. The {@link RedirectLoginSuccessHandler} is then used to redirect the client to the original request. The {@link RedirectLoginFailureHandler} is used to redirect the client to a failure page
 * in case of failed authentication.</li>
 * <li>{@code GET /ldap/logout} which signs the user out (i.e. remove the authentication token cookie).</li>
 * </ul>
 * 
 * <p>
 * Unlike {@link BasicRouterConfigurer}, {@link DigestRouterConfigurer} or {@link LDAPRouterConfigurer}, it is not possible to obtain a {@link RoleBasedAccessController} from a
 * {@link JWTSAuthentication} as it does not contain required user's information such as group membership.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@WebRoutes({
	@WebRoute(path = { "/login/ldap" }, method = { Method.GET }, produces = { "text/html" }),
	@WebRoute(path = { "/login/ldap" }, method = { Method.POST }),
	@WebRoute(path = { "/ldap/logout" }, method = { Method.GET })
})
@Bean( visibility = Bean.Visibility.PRIVATE )
public class LDAPRouterConfigurer implements WebRouteInterceptor.Configurer<SecurityContext.Intercepted<Identity, RoleBasedAccessController>>, WebRouter.Configurer<SecurityContext<Identity, RoleBasedAccessController>>, ErrorWebRouteInterceptor.Configurer<ExchangeContext> {

	private final Mono<? extends OCTJWK> jwsKey;
	private final JWSService jwsService;
	private final LDAPClient ldapClient;
	
	public LDAPRouterConfigurer(JWKService jwkService, JWSService jwsService, LDAPClient ldapClient) {
		this.jwsKey = jwkService.oct().generator()
			.algorithm(OCTAlgorithm.HS256.getAlgorithm())
			.generate()
			.cache();
		
		this.jwsService = jwsService;
		this.ldapClient = ldapClient;
	}
	
	@Override
	public WebRouteInterceptor<SecurityContext.Intercepted<Identity, RoleBasedAccessController>> configure(WebRouteInterceptor<SecurityContext.Intercepted<Identity, RoleBasedAccessController>> interceptors) {
		return interceptors
			// Cookie Token authentication (RFC7515 - JSON Web Signature (JWS)) + LDAP authentication + LDAP identity
			.intercept()
				.path("/ldap/**")
				.interceptors(List.of(SecurityInterceptor.of(
						new BearerTokenCredentialsExtractor<SecurityContext.Intercepted<Identity, RoleBasedAccessController>, WebExchange<SecurityContext.Intercepted<Identity, RoleBasedAccessController>>>()
							.or(new CookieTokenCredentialsExtractor<>()),
						new JWSAuthenticator<>(this.jwsService, LDAPAuthentication.class, this.jwsKey).failOnDenied().map(jwsAuthentication -> jwsAuthentication.getJws().getPayload()),
						new LDAPIdentityResolver(this.ldapClient),
						new GroupsRoleBasedAccessControllerResolver()
					),
					AccessControlInterceptor.authenticated()
				));
	}

	@Override
	public void configure(WebRouter<SecurityContext<Identity, RoleBasedAccessController>> routes) {
		routes
			// Form login + LDAP authenticator
			.route()
				.method(Method.GET)
				.path("/login/ldap")
				.produce(MediaTypes.TEXT_HTML)
				.handler(new FormLoginPageHandler<>("/login/ldap"))
			.route()
				.method(Method.POST)
				.path("/login/ldap")
				.handler(new LoginActionHandler<>(
					new FormCredentialsExtractor<>(),
					new LDAPAuthenticator(this.ldapClient, "dc=inverno,dc=io")
						.failOnDenied()
						.flatMap(authentication -> this.jwsService.builder(LDAPAuthentication.class, this.jwsKey)
							.header(header -> header
								.algorithm(OCTAlgorithm.HS256.getAlgorithm())
							)
							.payload(authentication)
							.build(MediaTypes.APPLICATION_JSON)
							.map(JWSAuthentication::new)
						),
					LoginSuccessHandler.of(
						new CookieTokenLoginSuccessHandler<>("/ldap"),
						new RedirectLoginSuccessHandler<>("/login/ldap")
					),
					new RedirectLoginFailureHandler<>("/login/ldap")
				))
			.route()
				.method(Method.GET)
				.path("/ldap/logout")
				.handler(new LogoutActionHandler<>(
					authentication -> Mono.empty(), // release the authentication: free resources, return a token to a pool...
					LogoutSuccessHandler.of(
						new CookieTokenLogoutSuccessHandler<>("/ldap"),
						new RedirectLogoutSuccessHandler<>()
					)
				));
	}

	@Override
	public ErrorWebRouteInterceptor<ExchangeContext> configure(ErrorWebRouteInterceptor<ExchangeContext> errorInterceptors) {
		return errorInterceptors
			// Form login + LDAP
			.interceptError()
				.error(UnauthorizedException.class)
				.path("/ldap/**")
				.interceptor(new FormAuthenticationErrorInterceptor<>("/login/ldap"));
	}
}
