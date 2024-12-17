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
package io.inverno.example.app_web_client.book;

import io.inverno.core.v1.Application;
import io.inverno.example.app_web_client.App_web_client;
import io.inverno.mod.configuration.source.BootstrapConfigurationSource;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

/**
 * <p>
 * Demonstrates how to use the Web client module to generate a declarative Web client and use it to consume a REST API.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 *
 * @see BookClient
 */
public class Main {

	private static final Logger LOGGER = LogManager.getLogger(Main.class);

	public static void main(String[] args) throws IOException {
		App_web_client app = Application.run(new App_web_client.Builder(new BootstrapConfigurationSource(Main.class.getModule(), args)));
		try {
			System.exit(new CommandLine(new BookCommands(app.bookClient())).execute(args));
		}
		finally {
			app.stop();
		}
	}
}
