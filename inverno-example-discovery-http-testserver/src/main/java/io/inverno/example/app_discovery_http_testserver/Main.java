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
package io.inverno.example.app_discovery_http_testserver;

import io.inverno.core.v1.Application;
import io.inverno.mod.configuration.source.BootstrapConfigurationSource;
import java.io.IOException;

/**
 * <p>
 * Starts a test HTTP server used to run the various Inverno discovery examples.
 * </p>
 *
 * <p>
 * The test server responds to any request with a {@link TestServerResponse} containing the request details (i.e. local and remote address, path, authority, headers...) in order to track how a request
 * is routed, load balanced or transformed.
 * </p>
 *
 * <p>
 * In order to properly test discovery modules, multiple test server instances can be started using different port as follows:
 * </p>
 *
 * <pre>{@code
 * $ mvn inverno:run -Dinverno.run.arguments="--io.inverno.example.app_discovery_http_testserver.configuration.http_server.server_port=8080"
 * }</pre>
 * <p>
 * or
 *
 * <pre>{@code
 * $ mvn install -Prelease
 * ...
 * $ ./target/inverno-example-http-discovery-testserver-1.0.0-SNAPSHOT-application_linux_amd64/bin/example-discovery-http-testserver --io.inverno.example.app_discovery_http_testserver.configuration.http_server.server_port=8080
 * }</pre>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
public class Main {

	public static void main(String[] args) throws IOException {
		Application.run(new App_discovery_http_testserver.Builder()
			.setAppConfigurationSource(new BootstrapConfigurationSource(Main.class.getModule(), args))
		);
	}
}
