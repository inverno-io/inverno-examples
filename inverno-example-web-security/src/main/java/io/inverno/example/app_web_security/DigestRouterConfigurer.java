package io.inverno.example.app_web_security;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.http.base.UnauthorizedException;
import io.inverno.mod.http.server.ExchangeContext;
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
import io.inverno.mod.web.ErrorWebRouter;
import io.inverno.mod.web.ErrorWebRouterConfigurer;
import io.inverno.mod.web.WebInterceptable;
import io.inverno.mod.web.WebInterceptorsConfigurer;
import java.util.List;

/**
 * <p>
 * This configurer configures the Web router to secure {@code /digest/**} routes using Digest authentication (RFC7616 - HTTP Digest Access Authentication).
 * </p>
 * 
 * <p>
 * The {@link SecurityInterceptor} is set on {@code /digest/**} routes using the {@link DigestCredentialsExtractor} to extract credentials and a {@link DigestUserAuthenticator} to authenticate
 * users. It also uses the {@link UserRoleBasedAccessControllerResolver} to provide a {@link RoleBasedAccessController} based on the groups the authenticated user belongs to. The
 * {@link BasicAuthenticationErrorInterceptor} is defined on {@code /digest/**} error routes to intercept {@link UnauthorizedException} ({@code 401}) and specifies the {@code www-authenticate} header
 * with Digest authentication scheme.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean( visibility = Bean.Visibility.PRIVATE )
public class DigestRouterConfigurer implements WebInterceptorsConfigurer<InterceptingSecurityContext<Identity, RoleBasedAccessController>>, ErrorWebRouterConfigurer<ExchangeContext> {

	private final CredentialsResolver<? extends LoginCredentials> credentialsResolver;
	
	public DigestRouterConfigurer(CredentialsResolver<? extends LoginCredentials> credentialsResolver) {
		this.credentialsResolver = credentialsResolver;
	}
	
	@Override
	public void configure(WebInterceptable<InterceptingSecurityContext<Identity, RoleBasedAccessController>, ?> interceptors) {
		interceptors
			.intercept()
				.path("/digest/**")
				.interceptors(List.of(new SecurityInterceptor<>(
						new DigestCredentialsExtractor(), 
						new PrincipalAuthenticator<>(this.credentialsResolver, new DigestCredentialsMatcher<>("secret"))
					),
					AccessControlInterceptor.authenticated()
				));
	}

	@Override
	public void configure(ErrorWebRouter<ExchangeContext> errorRouter) {
		errorRouter
			.intercept()
				.error(UnauthorizedException.class)
				.path("/digest/**")
				.interceptor(new DigestAuthenticationErrorInterceptor<>("inverno-digest", "secret"))
			.applyInterceptors(); // We must apply interceptors to intercept white labels error routes which are already defined 
	}
}
