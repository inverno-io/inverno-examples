/*
 * Copyright 2022 Jeremy Kuhn
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
package io.inverno.example.app_http_client.internal.command;

import java.util.Map;
import java.util.Optional;

import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.header.Headers;
import io.inverno.mod.http.client.Exchange;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * <p>
 *
 * </p>
 *
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
public abstract class AbstractHttpCommand implements Runnable {

	private final Method method;

	@ParentCommand
	protected HttpCommands httpCommands;

	@Option(
		names = {"-H", "--header"}, 
		description = "Header to include in the request when sending HTTP to a server"
	)
	private Map<String, String> headers = Map.of();

	@Option(
		names = {"-a", "--accept"}, 
		description = "Set the accept header"
	)
	private Optional<String> accept;

	@Option(
		names = {"-ae", "--accept-encoding"}, 
		description = "Set the accept-encoding header"
	)
	private Optional<String> acceptEncoding;

	@Option(
		names = {"-al", "--accept-language"}, 
		description = "Set the accept-language header"
	)
	private Optional<String> acceptLanguage;
	
	@Parameters(
		index= "0", 
		paramLabel = "path",
		description = "The path to the resource"
	)
	private String path;

	public AbstractHttpCommand(Method method) {
		this.method = method;
	}

	@Override
	public void run() {
		if(this.httpCommands.getEndpoint() == null) {
			this.httpCommands.getTerminal().writer().println("No connected endpoint");
			return;
		}
		
		try {
			this.httpCommands.getEndpoint()
				.exchange(this.method, this.path)
				.flatMap(exchange -> {
					exchange.request().headers(h -> {
						this.headers.forEach(h::add);
						this.accept.ifPresent(accept -> h.add(Headers.NAME_ACCEPT, accept));
						this.acceptEncoding.ifPresent(acceptEncoding -> h.add(Headers.NAME_ACCEPT_ENCODING, acceptEncoding));
						this.acceptLanguage.ifPresent(acceptLanguage -> h.add(Headers.NAME_ACCEPT_LANGUAGE, acceptLanguage));
					});
					
					this.configure(exchange);
					
					return exchange.response();
				})
				.flatMapMany(response -> response.body().raw().stream()) // We must consume the response payload data publisher to display logs
				.blockLast(); // request and response are logged in the interceptor that was specified when creating the endpoint (see HttpClientCommands) 
		}
		catch(Exception e) {
			e.printStackTrace(this.httpCommands.getTerminal().writer());
		}
		finally {
			this.httpCommands.getTerminal().writer().flush();
		}
	}

	protected abstract void configure(Exchange<ExchangeContext> exchange);
}
