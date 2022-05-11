package io.inverno.example.app_web_security;

import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Wrapper;
import io.inverno.mod.security.CredentialsResolver;
import io.inverno.mod.security.InMemoryUserCredentialsResolver;
import io.inverno.mod.security.UserCredentials;
import java.util.Map;
import java.util.function.Supplier;

@Wrapper
@Bean
public class AppCredentialsResolver implements Supplier<CredentialsResolver<UserCredentials>> {

	@Override
	public CredentialsResolver<UserCredentials> get() {
		return new InMemoryUserCredentialsResolver(Map.of("user", "password"));
	}
}
