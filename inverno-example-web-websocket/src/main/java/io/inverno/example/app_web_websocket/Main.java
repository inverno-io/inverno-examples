/*
 * Copyright 2022 Jeremy KUHN
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
package io.inverno.example.app_web_websocket;

import io.inverno.core.annotation.Bean;
import io.inverno.core.v1.Application;
import io.inverno.mod.configuration.ConfigurationKey.Parameter;
import io.inverno.mod.configuration.ConfigurationProperty;
import io.inverno.mod.configuration.ConfigurationSource;
import io.inverno.mod.configuration.source.BootstrapConfigurationSource;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import reactor.core.Disposable;

/**
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 */
public class Main {

	@Bean
	public static interface App_web_websocketConfigurationSource extends Supplier<ConfigurationSource<?, ?, ?>> {}
	
	@Bean
	public static interface App_web_websocketConfigurationParameters extends Supplier<List<Parameter>> {}
	
	public static Disposable disposable;
	
	public static void main(String[] args) throws IOException {
		BootstrapConfigurationSource bootstrapConfigurationSource = new BootstrapConfigurationSource(Main.class.getModule(), args);
		
		String profile = bootstrapConfigurationSource.get("profile")
			.execute()
			.map(result -> result.getResult().flatMap(ConfigurationProperty::asString).orElse("default"))
			.blockFirst();
		
		Application.run(new App_web_websocket.Builder()
			.setApp_web_websocketConfigurationSource(bootstrapConfigurationSource)
			.setApp_web_websocketConfigurationParameters(List.of(Parameter.of("profile", profile)))
		);
	}
}
