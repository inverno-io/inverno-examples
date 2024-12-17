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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * <p>
 * Represents a response from Discovery test server.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestServerResponse {

	private final String localAddress;
	private final String remoteAddress;
	private final String method;
	private final String path;
	private final String authority;
	private final Map<String, String> headers;

	private Map<String, String> responseHeaders;

	@JsonCreator
	public TestServerResponse(
		@JsonProperty("localAddress") String localAddress,
		@JsonProperty("remoteAddress") String remoteAddress,
		@JsonProperty("method") String method,
		@JsonProperty("path") String path,
		@JsonProperty("authority") String authority,
		@JsonProperty("headers") Map<String, String> headers
	) {
		this.localAddress = localAddress;
		this.remoteAddress = remoteAddress;
		this.method = method;
		this.path = path;
		this.authority = authority;
		this.headers = headers;
	}

	@JsonProperty("localAddress")
	public String getLocalAddress() {
		return localAddress;
	}

	@JsonProperty("remoteAddress")
	public String getRemoteAddress() {
		return remoteAddress;
	}

	@JsonProperty("method")
	public String getMethod() {
		return method;
	}

	@JsonProperty("path")
	public String getPath() {
		return path;
	}

	@JsonProperty("authority")
	public String getAuthority() {
		return authority;
	}

	@JsonProperty("headers")
	public Map<String, String> getHeaders() {
		return headers;
	}

	@JsonIgnore
	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}

	@JsonIgnore
	public void setResponseHeaders(Map<String, String> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	@Override
	public String toString() {
		return "TestServerResponse{" +
			"localAddress='" + localAddress + '\'' +
			", remoteAddress='" + remoteAddress + '\'' +
			", method='" + method + '\'' +
			", path='" + path + '\'' +
			", authority='" + authority + '\'' +
			", headers=" + headers +
			", responseHeaders=" + responseHeaders +
			'}';
	}
}