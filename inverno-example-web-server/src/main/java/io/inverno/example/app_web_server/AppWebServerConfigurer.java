/*
 * Copyright 2024 Jeremy Kuhn
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
package io.inverno.example.app_web_server;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.base.resource.ResourceService;
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.Status;
import io.inverno.mod.http.server.HttpAccessLogsInterceptor;
import io.inverno.mod.web.server.ContinueInterceptor;
import io.inverno.mod.web.server.ErrorWebRouter;
import io.inverno.mod.web.server.ErrorWebRouteInterceptor;
import io.inverno.mod.web.server.OpenApiRoutesConfigurer;
import io.inverno.mod.web.server.StaticHandler;
import io.inverno.mod.web.server.WebJarsRoutesConfigurer;
import io.inverno.mod.web.server.WebRouter;
import io.inverno.mod.web.server.WebRouteInterceptor;
import io.inverno.mod.web.server.WhiteLabelErrorRoutesConfigurer;
import io.inverno.mod.web.server.annotation.WebRoutes;
import io.inverno.mod.web.server.annotation.WebRoute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

/**
 * <p>
 * The Web server configurer used to configure route interceptors, static resource route, WebJars routes, OpenAPI routes, white label error routes...
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean(visibility = Bean.Visibility.PRIVATE)
@WebRoutes({
	@WebRoute( path = { "/static/{path:.*}" }, method = { Method.GET } ),
	@WebRoute( path = { "/hello" }, method = { Method.GET }, produces = { MediaTypes.TEXT_PLAIN }, language = {"en-US"} ),
	@WebRoute( path = { "/hello" }, method = { Method.GET }, produces = { MediaTypes.TEXT_PLAIN }, language = {"fr-FR"} ),
	@WebRoute( path = { "/custom_exception" } )
})
public class AppWebServerConfigurer implements WebRouteInterceptor.Configurer<InterceptorContext>, WebRouter.Configurer<InterceptorContext>, ErrorWebRouteInterceptor.Configurer<ExchangeContext>, ErrorWebRouter.Configurer<ExchangeContext> {

	private Logger logger = LogManager.getLogger(this.getClass());

	private final AppConfiguration configuration;
	private final ResourceService resourceService;

	public AppWebServerConfigurer(AppConfiguration configuration, ResourceService resourceService) {
		this.configuration = configuration;
		this.resourceService = resourceService;
	}

	@Override
	public WebRouteInterceptor<InterceptorContext> configure(WebRouteInterceptor<InterceptorContext> interceptors) {
		return interceptors
			.intercept()
				.interceptor(new HttpAccessLogsInterceptor<>())
			.intercept()
				.path("/continue")
				.interceptor(new ContinueInterceptor<>())
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

	@Override
	public void configure(WebRouter<InterceptorContext> routes) {
		routes
			.configureRoutes(new WebJarsRoutesConfigurer<>(this.resourceService))
			.configureRoutes(new OpenApiRoutesConfigurer<>(this.resourceService, true))
			.route()
			.path("/static/{path:.*}", true)
				.method(Method.GET)
				.handler(new StaticHandler<>(this.resourceService.getResource(this.configuration.web_root())))
			.route()
				.path("/hello")
				.method(Method.GET)
				.produce(MediaTypes.TEXT_PLAIN)
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
				.produce(MediaTypes.TEXT_PLAIN)
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
				.produce(MediaTypes.TEXT_PLAIN)
				.handler(exchange -> exchange
					.response()
					.body()
					.encoder(String.class)
					.value("Saluton!")
				)
			.route()
				.path("/continue")
				.method(Method.POST)
				.consume(MediaTypes.APPLICATION_JSON)
				.handler(exchange -> exchange.response().body().raw().stream(exchange.request().body().get().raw().stream()))
			.route()
				.path("/custom_exception")
				.handler(exchange -> {
					throw new SomeCustomException();
				});
	}

	@Override
	public ErrorWebRouteInterceptor<ExchangeContext> configure(ErrorWebRouteInterceptor<ExchangeContext> errorInterceptors) {
		return errorInterceptors
			.interceptError()
				.interceptor(new HttpAccessLogsInterceptor<>());
	}

	@Override
	public void configure(ErrorWebRouter<ExchangeContext> errorRoutes) {
		errorRoutes
			.configureErrorRoutes(new WhiteLabelErrorRoutesConfigurer<>())
			.routeError()
				.error(SomeCustomException.class)
				.handler(errorExchange -> errorExchange
					.response()
					.headers(headers -> headers
						.status(Status.BAD_REQUEST)
						.contentType(MediaTypes.TEXT_PLAIN)
					)
					.body()
					.encoder(String.class)
					.value("A custom exception was raised: " + errorExchange.context())
				);

	}
}
