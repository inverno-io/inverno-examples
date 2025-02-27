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
import io.inverno.mod.security.accesscontrol.RoleBasedAccessController;
import io.inverno.mod.security.authentication.CredentialsResolver;
import io.inverno.mod.security.authentication.LoginCredentialsMatcher;
import io.inverno.mod.security.authentication.user.User;
import io.inverno.mod.security.authentication.user.UserAuthenticator;
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
import io.inverno.mod.security.jose.jwt.JWTClaimsSet;
import io.inverno.mod.security.jose.jwt.JWTSAuthentication;
import io.inverno.mod.security.jose.jwt.JWTSAuthenticator;
import io.inverno.mod.security.jose.jwt.JWTService;
import io.inverno.mod.web.server.ErrorWebRouteInterceptor;
import io.inverno.mod.web.server.WebExchange;
import io.inverno.mod.web.server.WebRouteInterceptor;
import io.inverno.mod.web.server.WebRouter;
import io.inverno.mod.web.server.annotation.WebRoute;
import io.inverno.mod.web.server.annotation.WebRoutes;
import java.time.ZonedDateTime;
import java.util.List;
import reactor.core.publisher.Mono;

/**
 * <p>
 * This configurer configures the Web router to secure {@code /form/**} routes using Form based authentication.
 * </p>
 *
 * <p>
 * The {@link SecurityInterceptor} is set on {@code /form/**} routes using {@link BearerTokenCredentialsExtractor} or {@link CookieTokenCredentialsExtractor} to extract token credentials.
 * Authentication is done by verifying the token as a JSON Web Token. The {@link FormAuthenticationErrorInterceptor} is defined on {@code /form/**} error routes to intercept
 * {@link UnauthorizedException} ({@code 401}) and redirect the client to the form login page ({@code /login/form}).
 * </p>
 *
 * <p>
 * The configurer defines three routes:
 * </p>
 *
 * <ul>
 * <li>{@code GET /login/form} which displays the white label login page using the {@link FormLoginPageHandler}.</li>
 * <li>{@code POST /login/form} which signs the user in using a {@link LoginActionHandler} with {@link FormCredentialsExtractor} to extract user credentials and a {@link UserAuthenticator} with a
 * {@link LoginCredentialsMatcher} to authenticate the user. The resulting {@link io.inverno.mod.security.authentication.user.UserAuthentication} is then transformed into a {@link JWTSAuthentication}.
 * The {@link CookieTokenLoginSuccessHandler} sets the resulting token in a cookie. The {@link RedirectLoginSuccessHandler} is then used to redirect the client to the original request. The
 * {@link RedirectLoginFailureHandler} is used to redirect the client to a failure page in case of failed authentication.</li>
 * <li>{@code GET /form/logout} which signs the user out (i.e. remove the authentication token cookie).</li>
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
	@WebRoute(path = { "/login/form" }, method = { Method.GET }, produces = { "text/html" }),
	@WebRoute(path = { "/login/form" }, method = { Method.POST }),
	@WebRoute(path = { "/form/logout" }, method = { Method.GET })
})
@Bean( visibility = Bean.Visibility.PRIVATE )
public class FormRouterConfigurer implements WebRouteInterceptor.Configurer<SecurityContext.Intercepted<Identity, RoleBasedAccessController>>, WebRouter.Configurer<SecurityContext<Identity, RoleBasedAccessController>>, ErrorWebRouteInterceptor.Configurer<ExchangeContext> {

	private final CredentialsResolver<? extends User<Identity>> credentialsResolver;
	private final Mono<? extends OCTJWK> jwsKey;
	private final JWTService jwtService;
	
	public FormRouterConfigurer(CredentialsResolver<? extends User<Identity>> credentialsResolver, JWKService jwkService, JWTService jwtService) {
		this.credentialsResolver = credentialsResolver;
		
		this.jwsKey = jwkService.oct().generator()
			.algorithm(OCTAlgorithm.HS256.getAlgorithm())
			.generate()
			.cache();
		
		this.jwtService = jwtService;
	}

	@Override
	public WebRouteInterceptor<SecurityContext.Intercepted<Identity, RoleBasedAccessController>> configure(WebRouteInterceptor<SecurityContext.Intercepted<Identity, RoleBasedAccessController>> interceptors) {
		return interceptors
			// Cookie Token authentication (RFC7519 - JSON Web Token (JWT)) 
			.intercept()
				.path("/form/**")
				.interceptors(List.of(SecurityInterceptor.of(
						new BearerTokenCredentialsExtractor<SecurityContext.Intercepted<Identity, RoleBasedAccessController>, WebExchange<SecurityContext.Intercepted<Identity, RoleBasedAccessController>>>()
							.or(new CookieTokenCredentialsExtractor<>()),
						new JWTSAuthenticator<>(this.jwtService, this.jwsKey)
					),
					AccessControlInterceptor.authenticated()
				));
	}

	@Override
	public void configure(WebRouter<SecurityContext<Identity, RoleBasedAccessController>> routes) {
		routes
			.route()
				.method(Method.GET)
				.path("/login/form")
				.produce(MediaTypes.TEXT_HTML)
				.handler(new FormLoginPageHandler<>("/login/form"))
			.route()
				.method(Method.POST)
				.path("/login/form")
				.handler(new LoginActionHandler<>(
					new FormCredentialsExtractor<>(),
					new UserAuthenticator<>(this.credentialsResolver, new LoginCredentialsMatcher<>())
						.failOnDenied()
						.flatMap(authentication -> this.jwtService.jwsBuilder(this.jwsKey)
							.header(header -> header
								.algorithm(OCTAlgorithm.HS256.getAlgorithm())
							)
							.payload(JWTClaimsSet.of(authentication.getUsername(), ZonedDateTime.now().plusDays(1).toEpochSecond()).build())
							.build()
							.map(JWTSAuthentication::new)
						),
					LoginSuccessHandler.of(
						new CookieTokenLoginSuccessHandler<>("/form"),
						new RedirectLoginSuccessHandler<>()
					),
					new RedirectLoginFailureHandler<>("/login/form")
				))
			.route()
				.method(Method.GET)
				.path("/form/logout")
				.handler(new LogoutActionHandler<>(
					authentication -> Mono.empty(), // release the authentication: free resources, return a token to a pool...
					LogoutSuccessHandler.of(
						new CookieTokenLogoutSuccessHandler<>("/form"),
						new RedirectLogoutSuccessHandler<>()
					)
				));
			
	}

	@Override
	public ErrorWebRouteInterceptor<ExchangeContext> configure(ErrorWebRouteInterceptor<ExchangeContext> errorInterceptors) {
		return errorInterceptors
			.interceptError()
				.error(UnauthorizedException.class)
				.path("/form/**")
				.interceptor(new FormAuthenticationErrorInterceptor<>("/login/form"));
	}
}
