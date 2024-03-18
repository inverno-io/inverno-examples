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
package io.inverno.example.app_http;

import io.inverno.core.annotation.Bean;
import io.inverno.core.v1.Application;
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.server.ErrorExchange;
import io.inverno.mod.http.server.Exchange;
import io.inverno.mod.http.server.HttpServerConfiguration;
import io.inverno.mod.http.server.ServerController;
import java.net.URI;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.security.auth.x500.X500Principal;

/**
 * <p>
 * Starts an HTTP/2 server with TLS and HTTP compression using custom root and
 * error handlers.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
public class Main {

	/**
	 * <p>
	 * Socket bean for the server controller.
	 * </p>
	 *
	 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
	 */
	@Bean
	public static interface Controller extends Supplier<ServerController<ExchangeContext, Exchange<ExchangeContext>, ErrorExchange<ExchangeContext>>> {
	}

	public static void main(String[] args) {
		// Starts the server
		Application.run(new App_http.Builder()
			// Setups the server
			.setApp_httpConfiguration(
				App_httpConfigurationLoader.load(configuration -> configuration
					.http_server(server -> server
					// HTTP compression
					.decompression_enabled(true)
					.compression_enabled(true)
					// TLS
					.server_port(8443)
					.tls_enabled(true)
					.tls_key_store(URI.create("module://io.inverno.example.app_http/serverkeystore.p12"))
					.tls_key_store_type("pkcs12")
					.tls_key_store_password("password")
					// mTLS
					.tls_client_auth(HttpServerConfiguration.ClientAuth.REQUESTED)
					.tls_trust_store(URI.create("module://io.inverno.example.app_http/servertruststore.p12"))
					.tls_trust_store_type("pkcs12")
					.tls_trust_store_password("password")
					// Enable HTTP/2
					.h2_enabled(true)
					)
				)
			)
			// Sets the server controller
			.setController(ServerController.from(
				exchange -> {
					String message = "Hello " + exchange.request().getRemoteCertificates().map(certificates -> extractCN((X509Certificate)certificates[0])).orElse("from main") + "!";
					exchange.response()
						.body().string().value(message);
				},
				errorExchange -> {
					errorExchange.response()
						.headers(headers -> headers.status(500))
						.body().string().value("Error: " + errorExchange.getError().getMessage());
				}
			))
		);
	}
	
	public static String extractCN(X509Certificate certificate) {
		X500Principal principal = certificate.getSubjectX500Principal();
		List<String> names = new ArrayList<>();
		Pattern pattern = Pattern.compile("CN=([^,]+)");
		Matcher matcher = pattern.matcher(principal.getName());
		while (matcher.find()) {
			names.add(matcher.group(1));
		}
		return names.stream().collect(Collectors.joining(", "));
	}
}
