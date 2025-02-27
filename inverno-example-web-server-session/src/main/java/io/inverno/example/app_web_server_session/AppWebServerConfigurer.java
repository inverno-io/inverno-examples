/*
 * Copyright 2025 Jeremy KUHN
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
package io.inverno.example.app_web_server_session;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.resource.ModuleResource;
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.session.BasicSessionStore;
import io.inverno.mod.session.http.CookieSessionIdExtractor;
import io.inverno.mod.session.http.CookieSessionInjector;
import io.inverno.mod.session.http.SessionInterceptor;
import io.inverno.mod.session.http.context.BasicSessionContext;
import io.inverno.mod.web.server.ErrorWebRouter;
import io.inverno.mod.web.server.WebRouteInterceptor;
import io.inverno.mod.web.server.WebRouter;
import io.inverno.mod.web.server.WhiteLabelErrorRoutesConfigurer;
import java.net.URI;

/**
 * <p>
 * The Web server configurer configuring session support.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean( visibility = Bean.Visibility.PRIVATE )
public class AppWebServerConfigurer implements WebRouteInterceptor.Configurer<BasicSessionContext.Intercepted<AppSessionData>>, WebRouter.Configurer<ExchangeContext>, ErrorWebRouter.Configurer<ExchangeContext> {

	private final BasicSessionStore<AppSessionData> sessionStore;

	public AppWebServerConfigurer(BasicSessionStore<AppSessionData> sessionStore) {
		this.sessionStore = sessionStore;
	}

	@Override
	public WebRouteInterceptor<BasicSessionContext.Intercepted<AppSessionData>> configure(WebRouteInterceptor<BasicSessionContext.Intercepted<AppSessionData>> webRouteInterceptor) {
		return webRouteInterceptor
			.intercept()
				.interceptor(SessionInterceptor.of(
					new CookieSessionIdExtractor<>(),
					this.sessionStore,
					new CookieSessionInjector<>()
				));
	}

	@Override
	public void configure(WebRouter<ExchangeContext> webRouter) {
		webRouter
			.route()
				.path("/", true)
				.handler(exchange -> exchange.response().body().resource().value(new ModuleResource(URI.create("module://io.inverno.example.app_web_server_session/index.html"))));
	}

	@Override
	public void configure(ErrorWebRouter<ExchangeContext> errorRoutes) {
		errorRoutes
			.configureErrorRoutes(new WhiteLabelErrorRoutesConfigurer<>());
	}
}
