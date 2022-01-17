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
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.base.resource.ResourceService;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.web.ContinueInterceptor;
import io.inverno.mod.web.OpenApiRoutesConfigurer;
import io.inverno.mod.web.StaticHandler;
import io.inverno.mod.web.WebJarsRoutesConfigurer;
import io.inverno.mod.web.WebRoutable;
import io.inverno.mod.web.WebRouter;
import io.inverno.mod.web.WebRouterConfigurer;
import io.inverno.mod.web.WebRoutesConfigurer;
import io.inverno.mod.web.annotation.WebRoute;
import io.inverno.mod.web.annotation.WebRoutes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

/**
 *  
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
@Bean(visibility = Bean.Visibility.PRIVATE)
@WebRoutes({
	@WebRoute( path = { "/static/{path:.*}" }, method = { Method.GET } ),
	@WebRoute( path = { "/hello" }, method = { Method.GET }, produces = { MediaTypes.TEXT_PLAIN }, language = {"en-US"} ),
	@WebRoute( path = { "/hello" }, method = { Method.GET }, produces = { MediaTypes.TEXT_PLAIN }, language = {"fr-FR"} ),
	@WebRoute( path = { "/custom_exception" } )
})
public class App_webWebRoutesConfigurer implements WebRoutesConfigurer<InterceptorContext> {

	private final Logger logger = LogManager.getLogger(this.getClass());
	
	private final App_webConfiguration configuration;
	private final ResourceService resourceService;
	
	public App_webWebRoutesConfigurer(App_webConfiguration configuration, ResourceService resourceService) {
		this.configuration = configuration;
		this.resourceService = resourceService;
	}

	@Override
	public void accept(WebRoutable<InterceptorContext, ?> routable) {
		routable
			.configureRoutes(new WebJarsRoutesConfigurer(this.resourceService))
			.configureRoutes(new OpenApiRoutesConfigurer(this.resourceService, true))
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
						.value("L'intercepteur te dit: " + exchange.context().getInterceptorValue())
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
				.path("/continue")
				.method(Method.POST)
				.consumes(MediaTypes.APPLICATION_JSON)
				.handler(exchange -> exchange.response().body().raw().stream(exchange.request().body().get().raw().stream()))
			.route()
				.path("/custom_exception")
				.handler(exchange -> {
					throw new SomeCustomException();
				});
	}
}