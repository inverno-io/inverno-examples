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
package io.winterframework.example.app_http;

import java.net.URI;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import io.winterframework.core.annotation.Bean;
import io.winterframework.core.v1.Application;
import io.winterframework.mod.base.Charsets;
import io.winterframework.mod.http.server.ErrorExchange;
import io.winterframework.mod.http.server.Exchange;
import io.winterframework.mod.http.server.ExchangeHandler;

/**
 * <p>
 * Starts an HTTP/2 server with TLS and HTTP compression using custom root and
 * error handlers.
 * </p>
 * 
 * @author <a href="mailto:jeremy.kuhn@winterframework.io">Jeremy Kuhn</a>
 *
 */
public class Main {

	/**
	 * <p>
	 * Socket bean for the custom root handler.
	 * </p>
	 * 
	 * @author <a href="mailto:jeremy.kuhn@winterframework.io">Jeremy Kuhn</a>
	 */
	@Bean
	public static interface Handler extends Supplier<ExchangeHandler<Exchange>> {}
	
	/**
	 * <p>
	 * Socket bean for the custom error handler.
	 * </p>
	 * 
	 * @author <a href="mailto:jeremy.kuhn@winterframework.io">Jeremy Kuhn</a>
	 *
	 */
	@Bean
	public static interface ErrorHandler extends Supplier<ExchangeHandler<ErrorExchange<Throwable>>> {}

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
                        .key_store(URI.create("module:/keystore.jks"))
                        .key_store_password("password")
                        // Enable HTTP/2
                        .h2_enabled(true)
                    )
                )
            )
            // Sets the custom root handler
            .setHandler(exchange -> {
                exchange.response()
                    .body().raw().value(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello from main!", Charsets.DEFAULT)));
            })
            // Sets the custom error handler
            .setErrorHandler(exchange -> {
                exchange.response()
                   .headers(headers -> headers.status(500))
                   .body().raw().value(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Error: " + exchange.getError().getMessage(), Charsets.DEFAULT)));
            })
        );
    }
}
