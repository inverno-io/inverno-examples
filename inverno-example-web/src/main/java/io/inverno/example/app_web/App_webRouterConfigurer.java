/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.inverno.example.app_web;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.resource.MediaTypes;
import io.inverno.mod.base.resource.ResourceService;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.Status;
import io.inverno.mod.http.server.ExchangeContext;
import io.inverno.mod.web.ContinueInterceptor;
import io.inverno.mod.web.ErrorWebRoutable;
import io.inverno.mod.web.ErrorWebRoutesConfigurer;
import io.inverno.mod.web.OpenApiRoutesConfigurer;
import io.inverno.mod.web.StaticHandler;
import io.inverno.mod.web.WebInterceptable;
import io.inverno.mod.web.WebInterceptorsConfigurer;
import io.inverno.mod.web.WebJarsRoutesConfigurer;
import io.inverno.mod.web.WebRoutable;
import io.inverno.mod.web.WebRoutesConfigurer;
import io.inverno.mod.web.annotation.WebRoute;
import io.inverno.mod.web.annotation.WebRoutes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Mono;

/**
 *
 * @author jkuhn
 */
@Bean(visibility = Bean.Visibility.PRIVATE)
@WebRoutes({
	@WebRoute( path = { "/static/{path:.*}" }, method = { Method.GET } ),
	@WebRoute( path = { "/hello" }, method = { Method.GET }, produces = { MediaTypes.TEXT_PLAIN }, language = {"en-US"} ),
	@WebRoute( path = { "/hello" }, method = { Method.GET }, produces = { MediaTypes.TEXT_PLAIN }, language = {"fr-FR"} ),
	@WebRoute( path = { "/custom_exception" } )
})
public class App_webRouterConfigurer implements WebInterceptorsConfigurer<InterceptorContext>, WebRoutesConfigurer<InterceptorContext>, ErrorWebRoutesConfigurer<ExchangeContext> {

	private Logger logger = LogManager.getLogger(this.getClass());
	
	private final App_webConfiguration configuration;
	private final ResourceService resourceService;
	
	public App_webRouterConfigurer(App_webConfiguration configuration, ResourceService resourceService) {
		this.configuration = configuration;
		this.resourceService = resourceService;
	}
	
	@Override
	public void configure(WebInterceptable<InterceptorContext, ?> interceptors) {
		interceptors
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
	public void configure(WebRoutable<InterceptorContext, ?> routes) {
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

	@Override
	public void configure(ErrorWebRoutable<ExchangeContext, ?> errorRoutes) {
		errorRoutes
			.route()
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
