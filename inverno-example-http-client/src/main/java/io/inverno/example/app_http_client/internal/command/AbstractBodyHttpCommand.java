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

import io.inverno.mod.base.resource.FileResource;
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.header.Headers;
import io.inverno.mod.http.client.Exchange;
import io.inverno.mod.http.client.Request;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Option;
import reactor.core.publisher.Flux;

/**
 * <p>
 *
 * </p>
 *
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
public abstract class AbstractBodyHttpCommand extends AbstractHttpCommand {

	@Option(
		names = {"-ct", "--content-type"}, 
		description = "Set the content-type header"
	)
	private Optional<String> contentType;

	@ArgGroup(exclusive = true, multiplicity = "0..1")
	private Body body;
	
	public static class Body {
		@Option(
			names = {"-d", "--data"}, 
			description = {"Data to send in the request", "Use '@' to specify a path path to a local file" }
		)
		private String data;

		@ArgGroup( heading = "URL encoded Form\n", exclusive = false, multiplicity = "0..*")
		private List<URlEncodedParameter> urlEncodedParams;

		@ArgGroup(heading = "Multipart Form\n", exclusive = false, multiplicity = "0..*")
		private List<MultipartParameter> multipartParams;
	}

	public static class URlEncodedParameter {
	
		@Option(
			names = {"-pn", "--parameter-name"},
			description = "Set the parameter name",
			required = true
		)
		private String name;

		@Option(
			names = {"-pv", "--parameter-value"},
			description = "Set the parameter value",
			required = false
		)
		private String value;
	}

	public static class MultipartParameter {

		@Option(
			names = {"-fn", "--form-name"}, 
			description = "Set the form part name",
			required = true
		)
		private String name;

		@Option(
			names = {"-ff", "--form-filename"}, 
			description = {"Set the form part filename", "If a local file path is specified in the data parameter, this defaults to the name of the local file name"}
		)
		private String filename;

		@Option(
			names = {"-fH", "--form-header"}, 
			description = "Header to include in the form part"
		)
		private Map<String, String> headers = Map.of();

		@Option(
			names = {"-ft", "--form-type"}, 
			description = "Set the form part content-type"
		)
		private Optional<String> type;

		@Option(
			names = {"-fd", "--form-data"}, 
			description = {"Data to send in the form part", "Use '@' to specify a path path to a local file" }
		)
		private String data;
	}

	public AbstractBodyHttpCommand(Method method) {
		super(method);
	}

	@Override
	protected void configure(Exchange<ExchangeContext> exchange) {
		this.contentType.ifPresent(contentType -> exchange.request().headers(h -> h.add(Headers.NAME_CONTENT_TYPE, contentType)));
		
		if(this.body != null) {
			if(this.body.data != null) {
				configureData(exchange.request(), this.body.data);
			}
			else if(this.body.urlEncodedParams != null && !this.body.urlEncodedParams.isEmpty()) {
				configureUrlEncoded(exchange.request(), this.body.urlEncodedParams);
			}
			else if(this.body.multipartParams != null && !this.body.multipartParams.isEmpty()) {
				configureMultipart(exchange.request(), this.body.multipartParams);
			}
		}
	}

	private static void configureData(Request request, String data) {
		if(data.startsWith("@")) {
			request.body().resource().value(new FileResource(new File(data.substring(1))));
		}
		else {
			request.body().string().value(data);
		}
	}

	private static void configureUrlEncoded(Request request, List<URlEncodedParameter> urlEncodedParams) {
		request.body().urlEncoded()
			.from((factory, data) -> data.stream(Flux.fromStream(urlEncodedParams.stream()
				.map(param -> factory.create(param.name, param.value))
			)));
	}

	private static void configureMultipart(Request request, List<MultipartParameter> multipartParams) {
		request.body().multipart()
			.from((factory, data) -> data.stream(Flux.fromStream(multipartParams.stream()
				.map(param -> {
					if(param.data.startsWith("@")) {
						return factory.resource(part -> part
							.name(param.name)
							.filename(param.filename)
							.headers(h -> {
								param.headers.forEach(h::add);
								param.type.ifPresent(h::contentType);
							})
							.value(new FileResource(new File(param.data.substring(1))))
						);
					}
					else {
						return factory.string(part -> part
							.name(param.name)
							.filename(param.filename)
							.headers(h -> {
								param.headers.forEach(h::add);
								param.type.ifPresent(h::contentType);
							})
							.value(param.data)
						);
					}
				})
			)));
	}
}
