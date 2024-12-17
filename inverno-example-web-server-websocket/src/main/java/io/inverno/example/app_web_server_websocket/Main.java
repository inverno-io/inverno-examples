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
package io.inverno.example.app_web_server_websocket;

import io.inverno.core.annotation.Bean;
import io.inverno.core.v1.Application;
import io.inverno.mod.configuration.ConfigurationProperty;
import io.inverno.mod.configuration.ConfigurationSource;
import io.inverno.mod.configuration.source.BootstrapConfigurationSource;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

/**
 * <p>
 * Demonstrates how to expose a WebSocket endpoint using the Web server module.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
public class Main {

	public static void main(String[] args) throws IOException {
		BootstrapConfigurationSource bootstrapConfigurationSource = new BootstrapConfigurationSource(Main.class.getModule(), args);
		
		String profile = bootstrapConfigurationSource.get("profile")
			.execute()
			.map(result -> result.asString("default"))
			.blockFirst();
		
		Application.run(new App_web_server_websocket.Builder()
			.setAppConfigurationSource(bootstrapConfigurationSource.withParameters("profile", profile))
		);
	}
}
