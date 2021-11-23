/*
 * Copyright 2021 Jeremy KUHN
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
package io.inverno.example.app_web;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.web.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

/**
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
@Bean(visibility = Bean.Visibility.PRIVATE)
public class App_webWebInterceptorsConfigurer implements WebInterceptorsConfigurer<InterceptorContext> {

	private Logger logger = LogManager.getLogger(this.getClass());
	
	@Override
	public void accept(WebInterceptable<InterceptorContext, ?> router) {
		router
			.intercept()
				.path("/continue")
				.interceptor(new ContinueInterceptor())
			.intercept()
				.path("/hello")
				.interceptor(exchange -> {
					logger.info("Smile, you've been intercepted");
					return Mono.just(exchange);
				})
			.intercept()
				.path("/hello")
				.language("fr-FR")
				.interceptor(exchange -> {
					logger.info("Souriez, vous êtes interceptés");
					exchange.context().setInterceptorValue("Bonjour!");
					return Mono.just(exchange);
				});
	}
}
