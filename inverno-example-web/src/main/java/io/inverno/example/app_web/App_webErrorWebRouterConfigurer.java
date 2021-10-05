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
import io.inverno.mod.http.base.Status;
import io.inverno.mod.web.ErrorWebRouter;
import io.inverno.mod.web.ErrorWebRouterConfigurer;
import io.inverno.mod.web.WebExchange;

/**
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
@Bean
public class App_webErrorWebRouterConfigurer implements ErrorWebRouterConfigurer<WebExchange.Context> {

	@Override
	public void accept(ErrorWebRouter<WebExchange.Context> errorRouter) {
		errorRouter
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
