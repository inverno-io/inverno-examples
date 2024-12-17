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
package io.inverno.example.app_web_client;

import io.inverno.core.v1.Application;
import io.inverno.mod.configuration.source.BootstrapConfigurationSource;
import java.io.IOException;
import picocli.CommandLine;

/**
 * <p>
 * Demonstrates how to use the Web client module to create a universal HTTP client with HTTP service discovery, request routing, load balancing, transformation...
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
public class Main {

	public static void main(String[] args) throws IOException {
		App_web_client app = Application.run(new App_web_client.Builder(new BootstrapConfigurationSource(io.inverno.example.app_web_client.book.Main.class.getModule(), args)));
		try {
			System.exit(new CommandLine(new WebClientCommand(app.webClientService())).execute(args));
		}
		finally {
			app.stop();
		}
	}
}
