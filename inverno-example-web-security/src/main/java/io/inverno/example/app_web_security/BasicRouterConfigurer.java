package io.inverno.example.app_web_security;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.http.base.UnauthorizedException;
import io.inverno.mod.http.server.ExchangeContext;
import io.inverno.mod.security.accesscontrol.RoleBasedAccessController;
import io.inverno.mod.security.authentication.CredentialsResolver;
import io.inverno.mod.security.authentication.LoginCredentials;
import io.inverno.mod.security.authentication.LoginCredentialsMatcher;
import io.inverno.mod.security.authentication.PrincipalAuthenticator;
import io.inverno.mod.security.http.AccessControlInterceptor;
import io.inverno.mod.security.http.SecurityInterceptor;
import io.inverno.mod.security.http.basic.BasicAuthenticationErrorInterceptor;
import io.inverno.mod.security.http.basic.BasicCredentialsExtractor;
import io.inverno.mod.security.http.context.InterceptingSecurityContext;
import io.inverno.mod.security.http.context.SecurityContext;
import io.inverno.mod.security.identity.Identity;
import io.inverno.mod.web.ErrorWebRouter;
import io.inverno.mod.web.ErrorWebRouterConfigurer;
import io.inverno.mod.web.WebExchange;
import io.inverno.mod.web.WebInterceptable;
import io.inverno.mod.web.WebInterceptorsConfigurer;
import java.util.List;

/**
 * <p>
 * This configurer configures the Web router to secure {@code /basic/**} routes using Basic authentication (RFC7617 - The 'Basic' HTTP Authentication Scheme).
 * </p>
 * 
 * <p>
 * The {@link SecurityInterceptor} is set on {@code /basic/**} routes using the {@link BasicCredentialsExtractor} to extract credentials and a simple {@link LoginAuthenticator} to authenticate
 * users. It also uses the {@link UserRoleBasedAccessControllerResolver} to provide a {@link RoleBasedAccessController} based on the groups the authenticated user belongs to. The
 * {@link BasicAuthenticationErrorInterceptor} is defined on {@code /basic/**} error routes to intercept {@link UnauthorizedException} ({@code 401}) and specifies the {@code www-authenticate} header
 * with Basic authentication scheme.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean( visibility = Bean.Visibility.PRIVATE )
public class BasicRouterConfigurer implements WebInterceptorsConfigurer<InterceptingSecurityContext<Identity, RoleBasedAccessController>>, ErrorWebRouterConfigurer<ExchangeContext> {

	private final CredentialsResolver<? extends LoginCredentials> credentialsResolver;
	
	public BasicRouterConfigurer(CredentialsResolver<? extends LoginCredentials> credentialsResolver) {
		this.credentialsResolver = credentialsResolver;
	}
	
	@Override
	public void configure(WebInterceptable<InterceptingSecurityContext<Identity, RoleBasedAccessController>, ?> interceptors) {
 		interceptors
			.intercept()
				.path("/basic/**")
				.interceptors(List.of(new SecurityInterceptor<>(
						new BasicCredentialsExtractor(), 
						new PrincipalAuthenticator<>(this.credentialsResolver, new LoginCredentialsMatcher<>())
					),
					AccessControlInterceptor.authenticated()
				));
	}

	@Override
	public void configure(ErrorWebRouter<ExchangeContext> errorRouter) {
		errorRouter
			.intercept()
				.error(UnauthorizedException.class)
				.path("/basic/**")
				.interceptor(new BasicAuthenticationErrorInterceptor<>("inverno-basic"))
			.applyInterceptors(); // We must apply interceptors to intercept white labels error routes which are already defined 
	}
}
