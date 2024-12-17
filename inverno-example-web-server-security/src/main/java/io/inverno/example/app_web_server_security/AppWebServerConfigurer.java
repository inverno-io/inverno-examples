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
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.security.http.cors.CORSInterceptor;
import io.inverno.mod.security.http.csrf.CSRFDoubleSubmitCookieInterceptor;
import io.inverno.mod.web.server.ErrorWebRouter;
import io.inverno.mod.web.server.WebRouteInterceptor;
import io.inverno.mod.web.server.WhiteLabelErrorRoutesConfigurer;

/**
 * <p>
 * The Web server configurer configuring CORS and CSRF and configuring White-Label error routes.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean( visibility = Bean.Visibility.PRIVATE )
public class AppWebServerConfigurer implements WebRouteInterceptor.Configurer<ExchangeContext>, ErrorWebRouter.Configurer<ExchangeContext> {

	@Override
	public WebRouteInterceptor<ExchangeContext> configure(WebRouteInterceptor<ExchangeContext> interceptors) {
		return interceptors
			.intercept()
				.interceptor(CORSInterceptor.builder("http://127.0.0.1:9090").build())
			.intercept()
				.path("/csrf/**")
				.interceptor(CSRFDoubleSubmitCookieInterceptor.builder().build());
	}

	@Override
	public void configure(ErrorWebRouter<ExchangeContext> errorRoutes) {
		errorRoutes
			.configureErrorRoutes(new WhiteLabelErrorRoutesConfigurer<>());
	}
}
