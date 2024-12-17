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
package io.inverno.example.app_discovery_http;

import io.inverno.core.v1.Application;
import io.inverno.mod.configuration.source.BootstrapConfigurationSource;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

/**
 * <p>
 * Demonstrates how to use the HTTP service discovery to provide client-side routing, load balancing, request transformation...
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@CommandLine.Command(name = "example-discovery-http", mixinStandardHelpOptions = true, version = "1.0.0", description = "Inverno Discovery HTTP example")
public class Main implements Callable<Integer> {

	private static Logger LOGGER = LogManager.getLogger(Main.class);

	private final DiscoveryHttpClient discoveryHttpClient;

	@CommandLine.Option(
		names = {"-c", "--count"},
		description = "Set the number of requests to send",
		defaultValue = "1",
		required = false
	)
	private int count;

	@CommandLine.Option(
		names = {"-auth", "--authority"},
		description = "Set the request authority",
		required = false
	)
	private String authority;

	@CommandLine.Option(
		names = {"-H", "--header"},
		description = "Add a request header",
		required = false
	)
	private Map<String, String> headers = Map.of();

	@CommandLine.Parameters(
		index= "0",
		paramLabel = "uri",
		description = "The resource URI"
	)
	private URI uri;

	// For picocli-codegen
	public Main() {
		this.discoveryHttpClient = null;
	}

	public Main(DiscoveryHttpClient discoveryHttpClient) {
		this.discoveryHttpClient = discoveryHttpClient;
	}

	@Override
	public Integer call() throws Exception {
		final String requestCountFormat = "%" + Integer.toString(count).length() + "d";

		LOGGER.info("--------------------------------------------------------------------------------");
		this.discoveryHttpClient.get(this.uri, this.count, this.authority, this.headers)
			.doOnNext(LOGGER::info)
			.collect(Collectors.groupingBy(TestServerResponse::getLocalAddress))
			.doOnNext(responsesByAddress -> {
				LOGGER.info("--------------------------------------------------------------------------------");
				responsesByAddress.entrySet().stream().map(e -> String.format(requestCountFormat, e.getValue().size()) + " request(s) ----> " + e.getKey()).forEach(LOGGER::info);
				LOGGER.info("--------------------------------------------------------------------------------");
			})
			.block();
		return 0;
	}

	public static void main(String[] args) throws IOException {
		App_discovery_http app = Application.run(new App_discovery_http.Builder(new BootstrapConfigurationSource(Main.class.getModule(), args)));
		try {
			System.exit(new CommandLine(new Main(app.discoveryHttpClient())).execute(args));
		}
		finally {
			app.stop();
		}
	}
}
