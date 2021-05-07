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
package io.winterframework.example.app_web;

import io.winterframework.core.annotation.Bean;
import io.winterframework.mod.base.resource.MediaTypes;
import io.winterframework.mod.base.resource.ResourceService;
import io.winterframework.mod.http.base.Method;
import io.winterframework.mod.web.StaticHandler;
import io.winterframework.mod.web.WebExchange;
import io.winterframework.mod.web.WebRouter;
import io.winterframework.mod.web.WebRouterConfigurer;
import io.winterframework.mod.web.annotation.WebRoute;
import io.winterframework.mod.web.annotation.WebRoutes;

/**
 * 
 * @author <a href="mailto:jeremy.kuhn@winterframework.io">Jeremy Kuhn</a>
 *
 */
@Bean
@WebRoutes({
	@WebRoute( path = { "/static/{path:.*}" }, method = { Method.GET } ),
	@WebRoute( path = { "/hello" }, method = { Method.GET }, produces = { MediaTypes.TEXT_PLAIN }, language = {"en-US"} ),
	@WebRoute( path = { "/hello" }, method = { Method.GET }, produces = { MediaTypes.TEXT_PLAIN }, language = {"fr-FR"} ),
	@WebRoute( path = { "/custom_exception" } )
})
public class App_webWebRouterConfigurer implements WebRouterConfigurer<WebExchange> {

	private App_webConfiguration configuration;
	private ResourceService resourceService;
	
	public App_webWebRouterConfigurer(App_webConfiguration configuration, ResourceService resourceService) {
		this.configuration = configuration;
		this.resourceService = resourceService;
	}
	
	@Override
	public void accept(WebRouter<WebExchange> router) {
		router
			.route()
				.path("/static/{path:.*}", true)
				.method(Method.GET)
				.handler(new StaticHandler(this.resourceService.getResource(this.configuration.web_root())))
			.route()
				.path("/hello")
				.method(Method.GET)
				.produces(MediaTypes.TEXT_PLAIN)
				.language("en-US")
				.handler(exchange -> exchange
					.response()
						.body()
						.encoder(String.class)
						.value("Hello!")
				)
			.route()
				.path("/hello")
				.method(Method.GET)
				.produces(MediaTypes.TEXT_PLAIN)
				.language("fr-FR")
				.handler(exchange -> exchange
					.response()
						.body()
						.encoder(String.class)
						.value("Bonjour!")
				)
			.route()
				.path("/hello")
				.method(Method.GET)
				.produces(MediaTypes.TEXT_PLAIN)
				.handler(exchange -> exchange
					.response()
						.body()
						.encoder(String.class)
						.value("Saluton!")
				)
			.route()
				.path("/custom_exception")
				.handler(exchange -> {
					throw new SomeCustomException();
				});
	}
}
