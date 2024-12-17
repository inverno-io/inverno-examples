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
package io.inverno.example.web_server_modular.app;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.resource.ResourceService;
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.web.server.ErrorWebRouter;
import io.inverno.mod.web.server.OpenApiRoutesConfigurer;
import io.inverno.mod.web.server.WebJarsRoutesConfigurer;
import io.inverno.mod.web.server.WebRouter;
import io.inverno.mod.web.server.WhiteLabelErrorRoutesConfigurer;

/**
 * <p>
 * The Web server configurer configuring WebJars and OpenAPI routes as well as White-Label error routes.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean
public class AppWebServerConfigurer implements WebRouter.Configurer<ExchangeContext>, ErrorWebRouter.Configurer<ExchangeContext> {

	private final ResourceService resourceService;

	public AppWebServerConfigurer(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	@Override
	public void configure(WebRouter<ExchangeContext> routes) {
		routes
			.configureRoutes(new WebJarsRoutesConfigurer<>(this.resourceService))
			.configureRoutes(new OpenApiRoutesConfigurer<>(this.resourceService, true));
	}

	@Override
	public void configure(ErrorWebRouter<ExchangeContext> errorRoutes) {
		errorRoutes
			.configureErrorRoutes(new WhiteLabelErrorRoutesConfigurer<>());
	}
}
