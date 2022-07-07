package io.inverno.example.app_web_security;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.UnauthorizedException;
import io.inverno.mod.http.server.ExchangeContext;
import io.inverno.mod.ldap.LDAPClient;
import io.inverno.mod.security.accesscontrol.GroupsRoleBasedAccessControllerResolver;
import io.inverno.mod.security.accesscontrol.RoleBasedAccessController;
import io.inverno.mod.security.authentication.InvalidCredentialsException;
import io.inverno.mod.security.http.AccessControlInterceptor;
import io.inverno.mod.security.http.SecurityInterceptor;
import io.inverno.mod.security.http.LoginActionHandler;
import io.inverno.mod.security.http.LogoutActionHandler;
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
import io.inverno.mod.security.jose.jws.JWS;
import io.inverno.mod.security.jose.jws.JWSAuthentication;
import io.inverno.mod.security.jose.jws.JWSService;
import io.inverno.mod.security.jose.jwt.JWTSAuthentication;
import io.inverno.mod.security.ldap.authentication.LDAPAuthentication;
import io.inverno.mod.security.ldap.authentication.LDAPAuthenticator;
import io.inverno.mod.security.ldap.identity.LDAPIdentityResolver;
import io.inverno.mod.web.ErrorWebRouter;
import io.inverno.mod.web.ErrorWebRouterConfigurer;
import io.inverno.mod.web.WebExchange;
import io.inverno.mod.web.WebInterceptable;
import io.inverno.mod.web.WebInterceptorsConfigurer;
import io.inverno.mod.web.WebRoutable;
import io.inverno.mod.web.WebRoutesConfigurer;
import io.inverno.mod.web.annotation.WebRoute;
import io.inverno.mod.web.annotation.WebRoutes;
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
 * authenticated user and a {@link UserRoleBasedAccessControllerResolver} is used to provide a {@link RoleBasedAccessController} based on the groups the authenticated user belongs to. The
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
public class LDAPRouterConfigurer implements WebInterceptorsConfigurer<InterceptingSecurityContext<Identity, RoleBasedAccessController>>, WebRoutesConfigurer<SecurityContext<Identity, RoleBasedAccessController>>, ErrorWebRouterConfigurer<ExchangeContext> {

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
	public void configure(WebInterceptable<InterceptingSecurityContext<Identity, RoleBasedAccessController>, ?> interceptors) {
		interceptors
			// Cookie Token authentication (RFC7515 - JSON Web Signature (JWS)) + LDAP authentication + LDAP identity
			.intercept()
				.path("/ldap/**")
				.interceptors(List.of(new SecurityInterceptor<> (
						new BearerTokenCredentialsExtractor().or(new CookieTokenCredentialsExtractor()), 
						credentials -> this.jwsService.reader(LDAPAuthentication.class, this.jwsKey)
							.read(credentials.getToken(), MediaTypes.APPLICATION_JSON)
							.map(JWS::getPayload)
							.onErrorMap(e -> new InvalidCredentialsException("Invalid token", e)),
						new LDAPIdentityResolver(this.ldapClient),
						new GroupsRoleBasedAccessControllerResolver()
					),
					AccessControlInterceptor.authenticated()
				));
	}

	@Override
	public void configure(WebRoutable<SecurityContext<Identity, RoleBasedAccessController>, ?> routes) {
		routes
			// Form login + LDAP authenticator
			.route()
				.method(Method.GET)
				.path("/login/ldap")
				.produces(MediaTypes.TEXT_HTML)
				.handler(new FormLoginPageHandler<>("/login/ldap"))
			.route()
				.method(Method.POST)
				.path("/login/ldap")
				.handler(new LoginActionHandler<>(
					new FormCredentialsExtractor(), 
					new LDAPAuthenticator(this.ldapClient, "dc=inverno,dc=io")
						.flatMap(authentication -> this.jwsService.builder(LDAPAuthentication.class, this.jwsKey)
							.header(header -> header
								.algorithm(OCTAlgorithm.HS256.getAlgorithm())
							)
							.payload(authentication)
							.build(MediaTypes.APPLICATION_JSON)
							.map(JWSAuthentication::new)
						),
					new CookieTokenLoginSuccessHandler<JWSAuthentication<LDAPAuthentication>, SecurityContext<Identity, RoleBasedAccessController>, WebExchange<SecurityContext<Identity, RoleBasedAccessController>>>("/ldap")
						.andThen(new RedirectLoginSuccessHandler<>()),
					new RedirectLoginFailureHandler<>("/login/ldap")
				))
			.route()
				.method(Method.GET)
				.path("/ldap/logout")
				.handler(new LogoutActionHandler<>(
					authentication -> Mono.empty(), // release the authentication: free resources, return a token to a pool...
					new CookieTokenLogoutSuccessHandler<LDAPAuthentication, Identity, RoleBasedAccessController, SecurityContext<Identity, RoleBasedAccessController>, WebExchange<SecurityContext<Identity, RoleBasedAccessController>>>("/ldap")
						.andThen(new RedirectLogoutSuccessHandler<>())
				));
	}

	@Override
	public void configure(ErrorWebRouter<ExchangeContext> errorRouter) {
		errorRouter
			// Form login + LDAP
			.intercept()
				.error(UnauthorizedException.class)
				.path("/ldap/**")
				.interceptor(new FormAuthenticationErrorInterceptor<>("/login/ldap"))
			.applyInterceptors(); // We must apply interceptors to intercept white labels error routes which are already defined 
	}
}
