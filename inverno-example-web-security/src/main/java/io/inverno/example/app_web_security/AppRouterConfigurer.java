package io.inverno.example.app_web_security;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.UnauthorizedException;
import io.inverno.mod.http.server.ExchangeContext;
import io.inverno.mod.security.Authentication;
import io.inverno.mod.security.AuthenticationException;
import io.inverno.mod.security.CredentialsResolver;
import io.inverno.mod.security.UserAuthenticator;
import io.inverno.mod.security.UserCredentials;
import io.inverno.mod.security.http.AccessControlInterceptor;
import io.inverno.mod.security.http.AuthenticationInterceptor;
import io.inverno.mod.security.http.LoginActionHandler;
import io.inverno.mod.security.http.LogoutActionHandler;
import io.inverno.mod.security.http.basic.BasicAuthenticationErrorInterceptor;
import io.inverno.mod.security.http.basic.BasicCredentialsExtractor;
import io.inverno.mod.security.http.context.InterceptingSecurityContext;
import io.inverno.mod.security.http.context.SecurityContext;
import io.inverno.mod.security.http.digest.DigestAuthenticationErrorInterceptor;
import io.inverno.mod.security.http.digest.DigestCredentialsExtractor;
import io.inverno.mod.security.http.digest.DigestUserAuthenticator;
import io.inverno.mod.security.http.form.FormAuthenticationErrorInterceptor;
import io.inverno.mod.security.http.form.FormCredentialsExtractor;
import io.inverno.mod.security.http.form.FormLoginPageHandler;
import io.inverno.mod.security.http.form.RedirectLoginFailureHandler;
import io.inverno.mod.security.http.form.RedirectLoginSuccessHandler;
import io.inverno.mod.security.http.form.RedirectLogoutSuccessHandler;
import io.inverno.mod.security.http.token.CookieTokenCredentialsExtractor;
import io.inverno.mod.security.jose.jwa.OCTAlgorithm;
import io.inverno.mod.security.jose.jwk.JWKService;
import io.inverno.mod.security.jose.jwk.oct.OCTJWK;
import io.inverno.mod.security.jose.jws.JWS;
import io.inverno.mod.security.jose.jws.JWSService;
import io.inverno.mod.web.ErrorWebExchangeInterceptor;
import io.inverno.mod.web.ErrorWebRouter;
import io.inverno.mod.web.ErrorWebRouterConfigurer;
import io.inverno.mod.web.WebExchange;
import io.inverno.mod.web.WebExchangeHandler;
import io.inverno.mod.web.WebExchangeInterceptor;
import io.inverno.mod.web.WebInterceptable;
import io.inverno.mod.web.WebInterceptorsConfigurer;
import io.inverno.mod.web.WebRoutable;
import io.inverno.mod.web.WebRoutesConfigurer;
import io.inverno.mod.web.annotation.WebRoute;
import io.inverno.mod.web.annotation.WebRoutes;
import java.util.List;
import reactor.core.publisher.Mono;

@WebRoutes({
	@WebRoute(path = { "/" }, method = { Method.GET }, produces = { "text/html" }),
	@WebRoute(path = { "/login" }, method = { Method.GET }, produces = { "text/html" }),
	@WebRoute(path = { "/login" }, method = { Method.POST }),
	@WebRoute(path = { "/logout" }, method = { Method.GET })
})
@Bean( visibility = Bean.Visibility.PRIVATE )
public class AppRouterConfigurer implements WebInterceptorsConfigurer<InterceptingSecurityContext>, WebRoutesConfigurer<SecurityContext>, ErrorWebRouterConfigurer<ExchangeContext> {

	private final CredentialsResolver<UserCredentials> credentialsResolver;

	private final JWSService jwsService;
	private final Mono<? extends OCTJWK> jwsKey;
	
	public AppRouterConfigurer(CredentialsResolver<UserCredentials> credentialsResolver, JWSService jwsService, JWKService jwkService) {
		this.credentialsResolver = credentialsResolver;
		
		this.jwsKey = jwkService.oct().generator()
			.algorithm(OCTAlgorithm.HS256.getAlgorithm())
			.generate()
			.cache();
		
		this.jwsService = jwsService;
	}

	@Override
	public void configure(WebInterceptable<InterceptingSecurityContext, ?> interceptors) {
		interceptors
			.intercept()
				.path("/basic/**")
				.interceptors(List.of(
					WebExchangeInterceptor.wrap(new AuthenticationInterceptor<>(new BasicCredentialsExtractor(), new UserAuthenticator(this.credentialsResolver))),
					WebExchangeInterceptor.wrap(new AccessControlInterceptor<>())
				))
			.intercept()
				.path("/digest/**")
				.interceptors(List.of(
					WebExchangeInterceptor.wrap(new AuthenticationInterceptor<>(new DigestCredentialsExtractor(), new DigestUserAuthenticator(this.credentialsResolver, "secret"))),
					WebExchangeInterceptor.wrap(new AccessControlInterceptor<>())
				))
			.intercept()
				.path("/form/**")
				.interceptors(List.of(
					WebExchangeInterceptor.wrap(new AuthenticationInterceptor<>(
						new CookieTokenCredentialsExtractor(), 
						credentials -> this.jwsService.reader(String.class, this.jwsKey)
							.read(credentials.getToken(), MediaTypes.TEXT_PLAIN)
							.map(jws -> Authentication.granted())
							.onErrorMap(t -> new AuthenticationException("Invalid token", t))
					)),
					WebExchangeInterceptor.wrap(new AccessControlInterceptor<>())
				));
	}

	@Override
	public void configure(WebRoutable<SecurityContext, ?> routes) {
		routes
			.route()
				.method(Method.GET)
				.path("/")
				.produces(MediaTypes.TEXT_HTML)
				.handler(exchange -> exchange.response().body().string().value("<html><head><title>Hello</title></head><body><h1>Hello</h1></body></html>"))
			.route()
				.method(Method.GET)
				.path("/login")
				.produces(MediaTypes.TEXT_HTML)
				.handler(WebExchangeHandler.wrap(new FormLoginPageHandler<>()))
			.route()
				.method(Method.POST)
				.path("/login")
				.handler(WebExchangeHandler.wrap(new LoginActionHandler<>(
					new FormCredentialsExtractor(), 
					new UserAuthenticator(this.credentialsResolver), 
					new RedirectLoginSuccessHandler<Authentication, SecurityContext, WebExchange<SecurityContext>>()
						.compose((exchange, authentication) -> this.jwsService.builder(String.class, this.jwsKey)
							.header(jwsHeader -> jwsHeader.algorithm(OCTAlgorithm.HS256.getAlgorithm()))
							.payload("user") // TODO we should get the authenticated user name from the authentication => the user authenticator should return a UserAuthentication
							.build(MediaTypes.TEXT_PLAIN)
							.map(JWS::toCompact)
							.doOnNext(token -> exchange.response().cookies(cookies -> cookies.addCookie(c -> c.secure(false).httpOnly(true).name("auth-token").value(token))))
							.then()
						),
					new RedirectLoginFailureHandler<>()
				)))
			.route()
				.method(Method.GET)
				.path("/logout")
				.handler(WebExchangeHandler.wrap(new LogoutActionHandler<>(
					authentication -> Mono.empty(), // release the authentication: free resources, return a token to a pool...
					new RedirectLogoutSuccessHandler<Authentication, SecurityContext, WebExchange<SecurityContext>>()
						.compose((exchange, authentication) -> Mono.fromRunnable(() -> {
							exchange.response().cookies(cookies -> cookies.addCookie(c -> c.name("auth-token").value("").maxAge(0)));
						}))
				)));
	}

	@Override
	public void configure(ErrorWebRouter<ExchangeContext> errorRouter) {
		errorRouter
			.intercept()
				.error(UnauthorizedException.class)
				.path("/basic/**")
				.interceptor(ErrorWebExchangeInterceptor.wrap(new BasicAuthenticationErrorInterceptor<>("inverno-basic")))
			.intercept()
				.error(UnauthorizedException.class)
				.path("/digest/**")
				.interceptor(ErrorWebExchangeInterceptor.wrap(new DigestAuthenticationErrorInterceptor<>("inverno-digest", "secret")))
			.intercept()
				.error(UnauthorizedException.class)
				.path("/form/**")
				.interceptor(ErrorWebExchangeInterceptor.wrap(new FormAuthenticationErrorInterceptor<>()))
			// We must do this to intercept white labels error routes 
			.applyInterceptors(); 
	}
}
