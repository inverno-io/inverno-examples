package io.inverno.example.app_web_security;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.UnauthorizedException;
import io.inverno.mod.http.server.ExchangeContext;
import io.inverno.mod.security.accesscontrol.RoleBasedAccessController;
import io.inverno.mod.security.authentication.CredentialsResolver;
import io.inverno.mod.security.authentication.LoginCredentialsMatcher;
import io.inverno.mod.security.authentication.user.User;
import io.inverno.mod.security.authentication.user.UserAuthenticator;
import io.inverno.mod.security.http.AccessControlInterceptor;
import io.inverno.mod.security.http.login.LoginActionHandler;
import io.inverno.mod.security.http.login.LoginSuccessHandler;
import io.inverno.mod.security.http.login.LogoutActionHandler;
import io.inverno.mod.security.http.login.LogoutSuccessHandler;
import io.inverno.mod.security.http.SecurityInterceptor;
import io.inverno.mod.security.http.context.InterceptingSecurityContext;
import io.inverno.mod.security.http.context.SecurityContext;
import io.inverno.mod.security.http.form.FormAuthenticationErrorInterceptor;
import io.inverno.mod.security.http.form.FormCredentialsExtractor;
import io.inverno.mod.security.http.form.FormLoginPageHandler;
import io.inverno.mod.security.http.form.RedirectLoginFailureHandler;
import io.inverno.mod.security.http.form.RedirectLoginSuccessHandler;
import io.inverno.mod.security.http.form.RedirectLogoutSuccessHandler;
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
import io.inverno.mod.web.ErrorWebRouter;
import io.inverno.mod.web.ErrorWebRouterConfigurer;
import io.inverno.mod.web.WebInterceptable;
import io.inverno.mod.web.WebInterceptorsConfigurer;
import io.inverno.mod.web.WebRoutable;
import io.inverno.mod.web.WebRoutesConfigurer;
import io.inverno.mod.web.annotation.WebRoute;
import io.inverno.mod.web.annotation.WebRoutes;
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
 * <li>{@code POST /login/form} which signs the user in using a {@link LoginActionHandler} with {@link FormCredentialsExtractor} to extract user credentials and a simple {@link LoginAuthenticator} to
 * authenticate the user. The resulting {@link LoginAuthentication} is then transformed into a {@link JWTSAuthentication}. The {@link CookieTokenLoginSuccessHandler} sets the resulting token in a
 * cookie. The {@link RedirectLoginSuccessHandler} is then used to redirect the client to the original request. The {@link RedirectLoginFailureHandler} is used to redirect the client to a failure page
 * in case of failed authentication.</li>
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
public class FormRouterConfigurer implements WebInterceptorsConfigurer<InterceptingSecurityContext<Identity, RoleBasedAccessController>>, WebRoutesConfigurer<SecurityContext<Identity, RoleBasedAccessController>>, ErrorWebRouterConfigurer<ExchangeContext> {

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
	public void configure(WebInterceptable<InterceptingSecurityContext<Identity, RoleBasedAccessController>, ?> interceptors) {
		interceptors
			// Cookie Token authentication (RFC7519 - JSON Web Token (JWT)) 
			.intercept()
				.path("/form/**")
				.interceptors(List.of(SecurityInterceptor.of(
						new BearerTokenCredentialsExtractor()
							.or(new CookieTokenCredentialsExtractor()), 
						new JWTSAuthenticator<>(this.jwtService, this.jwsKey)
					),
					AccessControlInterceptor.authenticated()
				));
	}

	@Override
	public void configure(WebRoutable<SecurityContext<Identity, RoleBasedAccessController>, ?> routes) {
		routes
			.route()
				.method(Method.GET)
				.path("/login/form")
				.produces(MediaTypes.TEXT_HTML)
				.handler(new FormLoginPageHandler<>("/login/form"))
			.route()
				.method(Method.POST)
				.path("/login/form")
				.handler(new LoginActionHandler<>(
					new FormCredentialsExtractor(), 
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
	public void configure(ErrorWebRouter<ExchangeContext> errorRouter) {
		errorRouter
			.intercept()
				.error(UnauthorizedException.class)
				.path("/form/**")
				.interceptor(new FormAuthenticationErrorInterceptor<>("/login/form"))
			.applyInterceptors(); // We must apply interceptors to intercept white labels error routes which are already defined 
	}
}
