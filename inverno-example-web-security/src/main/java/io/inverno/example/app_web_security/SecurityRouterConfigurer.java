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
package io.inverno.example.app_web_security;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.http.server.ExchangeContext;
import io.inverno.mod.security.http.cors.CORSInterceptor;
import io.inverno.mod.security.http.csrf.CSRFDoubleSubmitCookieInterceptor;
import io.inverno.mod.web.WebInterceptable;
import io.inverno.mod.web.WebInterceptorsConfigurer;

/**
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean( visibility = Bean.Visibility.PRIVATE )
public class SecurityRouterConfigurer implements WebInterceptorsConfigurer<ExchangeContext> {

	@Override
	public void configure(WebInterceptable<ExchangeContext, ?> interceptors) {
		interceptors
			.intercept()
				.interceptor(CORSInterceptor.builder("http://127.0.0.1:9090").build())
			.intercept()
				.path("/csrf/**")
				.interceptor(CSRFDoubleSubmitCookieInterceptor.builder().build());
	}
}
