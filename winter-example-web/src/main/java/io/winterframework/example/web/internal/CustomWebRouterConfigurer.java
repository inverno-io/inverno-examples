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
package io.winterframework.example.web.internal;

import io.winterframework.core.annotation.Bean;
import io.winterframework.mod.web.Method;
import io.winterframework.mod.web.router.WebExchange;
import io.winterframework.mod.web.router.WebRouter;
import io.winterframework.mod.web.router.WebRouterConfigurer;
import io.winterframework.mod.web.router.annotation.WebRoute;
import io.winterframework.mod.web.router.annotation.WebRoutes;

/**
 * @author jkuhn
 *
 */
@WebRoutes({
	@WebRoute(path = { "/some|get" }, method = { Method.GET }, produces = { "application/json" })
})
@Bean
public class CustomWebRouterConfigurer implements WebRouterConfigurer<WebExchange> {

	@Override
	public void accept(WebRouter<WebExchange> router) {
		router.route().path("/some|get", false).method(Method.GET).produces("application/json").handler(exchange -> {
			exchange.response().body().encoder(String.class).value("Some Get");
		});
	}
}
