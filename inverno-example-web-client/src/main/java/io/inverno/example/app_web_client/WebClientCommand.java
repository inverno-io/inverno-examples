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

import io.inverno.mod.http.base.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import picocli.CommandLine;

/**
 * <p>
 * Web client command line.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@CommandLine.Command(name = "example-web-client", mixinStandardHelpOptions = true, version = "1.0.0", description = "Inverno Web Client example")
public class WebClientCommand implements Callable<Integer> {

	private final WebClientService webClientService;

	@CommandLine.Option(
		names = {"-m", "--method"},
		description = "Set the HTTP request method Header",
		defaultValue = "GET"
	)
	private Method method;

	@CommandLine.Option(
		names = {"-auth", "--authority"},
		description = "Set the request authority"
	)
	private Optional<String> authority;

	@CommandLine.Option(
		names = {"-H", "--header"},
		description = "Header to include in the request when sending HTTP to a server"
	)
	private Map<String, String> headers = Map.of();

	@CommandLine.Option(
		names = {"-ct", "--content-type"},
		description = "Set the content-type header"
	)
	private Optional<String> contentType;

	@CommandLine.Option(
		names = {"-a", "--accept"},
		description = "Set the accept header"
	)
	private Optional<String> accept;

	@CommandLine.Option(
		names = {"-ae", "--accept-encoding"},
		description = "Set the accept-encoding header"
	)
	private Optional<String> acceptEncoding;

	@CommandLine.Option(
		names = {"-al", "--accept-language"},
		description = "Set the accept-language header"
	)
	private Optional<String> acceptLanguage;

	@CommandLine.ArgGroup(exclusive = true, multiplicity = "0..1")
	private Body body;

	public static class Body {
		@CommandLine.Option(
			names = {"-d", "--data"},
			description = {"Data to send in the request", "Use '@' to specify a path path to a local file" }
		)
		private String data;

		@CommandLine.ArgGroup( heading = "URL encoded Form\n", exclusive = false, multiplicity = "0..*")
		private List<URlEncodedParameter> urlEncodedParams;

		@CommandLine.ArgGroup(heading = "Multipart Form\n", exclusive = false, multiplicity = "0..*")
		private List<MultipartParameter> multipartParams;

		public String getData() {
			return data;
		}

		public List<URlEncodedParameter> getUrlEncodedParams() {
			return urlEncodedParams;
		}

		public List<MultipartParameter> getMultipartParams() {
			return multipartParams;
		}
	}

	public static class URlEncodedParameter {

		@CommandLine.Option(
			names = {"-pn", "--parameter-name"},
			description = "Set the parameter name",
			required = true
		)
		private String name;

		@CommandLine.Option(
			names = {"-pv", "--parameter-value"},
			description = "Set the parameter value",
			required = false
		)
		private String value;

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}
	}

	public static class MultipartParameter {

		@CommandLine.Option(
			names = {"-fn", "--form-name"},
			description = "Set the form part name",
			required = true
		)
		private String name;

		@CommandLine.Option(
			names = {"-ff", "--form-filename"},
			description = {"Set the form part filename", "If a local file path is specified in the data parameter, this defaults to the name of the local file name"}
		)
		private String filename;

		@CommandLine.Option(
			names = {"-fH", "--form-header"},
			description = "Header to include in the form part"
		)
		private Map<String, String> headers = Map.of();

		@CommandLine.Option(
			names = {"-ft", "--form-type"},
			description = "Set the form part content-type"
		)
		private Optional<String> type;

		@CommandLine.Option(
			names = {"-fd", "--form-data"},
			description = {"Data to send in the form part", "Use '@' to specify a path path to a local file" }
		)
		private String data;

		public String getName() {
			return name;
		}

		public String getFilename() {
			return filename;
		}

		public Map<String, String> getHeaders() {
			return headers;
		}

		public Optional<String> getType() {
			return type;
		}

		public String getData() {
			return data;
		}
	}

	@CommandLine.Parameters(
		index= "0",
		paramLabel = "uri",
		description = "The resource URI"
	)
	private URI uri;

	// For picocli-codegen
	public WebClientCommand() {
		this.webClientService = null;
	}

	public WebClientCommand(WebClientService webClientService) {
		this.webClientService = webClientService;
	}

	// When following redirect
	WebClientCommand(WebClientCommand original, String location) {
		this.webClientService = original.webClientService;

		this.method = original.method;
		this.uri = URI.create(location);
		this.authority = original.authority;
		this.headers = original.headers;
		this.contentType = original.contentType;
		this.accept = original.accept;
		this.acceptEncoding = original.acceptEncoding;
		this.body = original.body;
		this.acceptLanguage = original.acceptLanguage;
	}

	@Override
	public Integer call() throws Exception {
		try {
			this.webClientService.executeCommand(this);
			return 0;
		}
		catch(Throwable t) {
			t.printStackTrace();
			return 1;
		}
	}

	public Method getMethod() {
		return method;
	}

	public URI getURI() {
		return uri;
	}

	public Optional<String> getAuthority() {
		return authority;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public Optional<String> getContentType() {
		return contentType;
	}

	public Optional<String> getAccept() {
		return accept;
	}

	public Optional<String> getAcceptEncoding() {
		return acceptEncoding;
	}

	public Optional<String> getAcceptLanguage() {
		return acceptLanguage;
	}

	public Body getBody() {
		return body;
	}
}
