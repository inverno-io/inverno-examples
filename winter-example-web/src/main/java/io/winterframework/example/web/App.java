/*
 * Copyright 2020 Jeremy KUHN
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
package io.winterframework.example.web;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import io.winterframework.core.annotation.Bean;
import io.winterframework.core.v1.Application;
import io.winterframework.mod.configuration.ConfigurationKey.Parameter;
import io.winterframework.mod.configuration.ConfigurationSource;
import io.winterframework.mod.configuration.source.ApplicationConfigurationSource;

/**
 * @author <a href="mailto:jeremy.kuhn@winterframework.io">Jeremy Kuhn</a>
 *
 */
public class App {
	
	@Bean
	public static interface ConfigurationParameters extends Supplier<List<Parameter>> {}

	@Bean
	public static interface AppConfigurationSource extends Supplier<ConfigurationSource<?, ?, ?>> {}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	public static void main(String[] args) throws IllegalStateException, IOException, NoSuchMethodException, SecurityException {
		Application.with(new Web.Builder()
			.setAppConfigurationSource(new ApplicationConfigurationSource(App.class.getModule(), args))
			.setConfigurationParameters(List.of(Parameter.of("profile", System.getProperty("profile", "default"))))
//			.setRootHandler(configuration0())
//			.setErrorHandler(error())
		).run();
		
//		Boot boot = new Boot.Builder().build();
//		boot.start();
		
		/*Application.with(new io.winterframework.mod.web.Web.Builder(boot.netService(), boot.resourceService(), List.of(boot.jsonMediaTypeConverter(), boot.textPlainMediaTypeConverter()))
			.setWebConfiguration(WebConfigurationLoader.load(conf -> conf.web(http_conf -> http_conf.server_port(8080))))
			.setWebRouterConfigurer(router -> router
				.route()
					.path("/path/to/resource1")
					.method(Method.GET)
					.produces(MediaTypes.APPLICATION_JSON)
					.produces(MediaTypes.TEXT_PLAIN)
					.handler(exchange -> exchange
						.response()
						.body()
						.encoder()
						.value("Resource 1")
					)
				.route()
					.path("/path/to/resource2")
					.method(Method.GET)
					.produces(MediaTypes.APPLICATION_JSON)
					.produces(MediaTypes.TEXT_PLAIN)
					.handler(exchange -> exchange
						.response()
						.body()
						.encoder()
						.value("Resource 2")
					)
			)
		).run();*/
		
		/*Application.with(new Server.Builder(boot.netService(), boot.resourceService())
			.setHttpServerConfiguration(HttpServerConfigurationLoader.load(conf -> conf.server_port(8080)))
			.setRootHandler(
				exchange -> exchange
					.response()
					.body()
					.raw()
					.value(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello, world!", Charsets.DEFAULT)))
			)
		).run();*/
		
	}	
}
