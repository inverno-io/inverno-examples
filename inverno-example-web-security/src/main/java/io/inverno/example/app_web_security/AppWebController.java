package io.inverno.example.app_web_security;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.security.http.context.SecurityContext;
import io.inverno.mod.web.annotation.WebController;
import io.inverno.mod.web.annotation.WebRoute;

@Bean
@WebController
public class AppWebController {

	@WebRoute(path = "/hello", produces = MediaTypes.TEXT_PLAIN)
	public String public_hello(SecurityContext securityContext) {
		return "Hello everyone";
	}

	@WebRoute(path = "/basic/hello", produces = MediaTypes.TEXT_PLAIN)
	public String basic_hello(SecurityContext securityContext) {
		return "Hello basic";
	}

	@WebRoute(path = "/digest/hello", produces = MediaTypes.TEXT_PLAIN)
	public String digest_hello(SecurityContext securityContext) {
		return "Hello digest";
	}

	@WebRoute(path = "/form/hello", produces = MediaTypes.TEXT_PLAIN)
	public String form_hello(SecurityContext securityContext) {
		return "Form digest";
	}
}
