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
package io.inverno.example.app_http_server_websocket;

import io.inverno.core.v1.Application;
import java.net.URI;

/**
 * <p>
 * Demonstrates how to expose a WebSocket endpoint using the HTTP server module.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
public class Main {

	public static void main(String[] args) {
		// Starts the server
		Application.run(new App_http_server_websocket.Builder()
			// Setups the server
			.setAppConfiguration(
				AppConfigurationLoader.load(configuration -> configuration
					.http_server(server -> server
						// TLS
						/*.server_port(8443)
						.tls_enabled(true)
						.tls_key_store(URI.create("module:/serverkeystore.p12"))
						.tls_key_store_type("pkcs12") 
						.tls_key_store_password("password")*/
						// WebSocket
						//.ws_max_frame_size(16)
						.ws_message_compression_enabled(true)
						.ws_message_compression_level(6)
					)
				)
			)
		);
	}
}
