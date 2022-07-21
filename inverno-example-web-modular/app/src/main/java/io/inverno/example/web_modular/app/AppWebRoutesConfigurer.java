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
package io.inverno.example.web_modular.app;

import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.resource.ResourceService;
import io.inverno.mod.http.server.ExchangeContext;
import io.inverno.mod.web.OpenApiRoutesConfigurer;
import io.inverno.mod.web.WebJarsRoutesConfigurer;
import io.inverno.mod.web.WebRoutable;
import io.inverno.mod.web.WebRoutesConfigurer;

/**
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
@Bean
public class AppWebRoutesConfigurer implements WebRoutesConfigurer<ExchangeContext> {

	private final ResourceService resourceService;
	
	public AppWebRoutesConfigurer(ResourceService resourceService) {
		this.resourceService = resourceService;
	}
	
	@Override
	public void configure(WebRoutable<ExchangeContext, ?> routable) {
		routable
			.configureRoutes(new WebJarsRoutesConfigurer<>(this.resourceService))
			.configureRoutes(new OpenApiRoutesConfigurer<>(this.resourceService, true));
	}
}
