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
import io.winterframework.mod.base.resource.FileResource;
import io.winterframework.mod.base.resource.MediaTypes;
import io.winterframework.mod.http.base.Method;
import io.winterframework.mod.web.StaticHandler;
import io.winterframework.mod.web.WebExchange;
import io.winterframework.mod.web.WebRouter;
import io.winterframework.mod.web.WebRouterConfigurer;
import io.winterframework.mod.web.annotation.WebRoute;
import io.winterframework.mod.web.annotation.WebRoutes;

/**
 * @author <a href="mailto:jeremy.kuhn@winterframework.io">Jeremy Kuhn</a>
 *
 */
@WebRoutes({
	@WebRoute(path = { "/some_get" }, method = { Method.GET }, produces = { MediaTypes.APPLICATION_JSON })
})
@Bean
public class CustomWebRouterConfigurer implements WebRouterConfigurer<WebExchange> {

	public interface SecurityContext {
		boolean isAuthenticated();
	}
	
	public interface Book {
		String getId();
	}
	
	public class Result {
		
		public Result(String message) {
		}
		
		public String getMessage() {
			return null;
		}
	}
	
	public interface BookRepository {
	}
	
	public class SearchResult {
		
		
	}
	
	@Override
	public void accept(WebRouter<WebExchange> router) {
		router.route().path("/some_get", false).method(Method.GET).produces(MediaTypes.APPLICATION_JSON).handler(exchange -> {
			exchange.response().body().encoder(String.class).value("Some Get");
		});
		
		router.route()
			.path("/static/{path:.*}")
			.handler(new StaticHandler(new FileResource("/home/jkuhn/Devel/git/frmk/io.winterframework.example.app_web/web-root/")));
	}
	
	public Result storeBook(Book book) {
		return null;
	}
}
