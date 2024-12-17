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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.inverno.core.annotation.Bean;
import io.inverno.mod.discovery.ServiceID;
import io.inverno.mod.discovery.ServiceNotFoundException;
import io.inverno.mod.discovery.http.HttpDiscoveryService;
import io.inverno.mod.discovery.http.HttpTrafficPolicy;
import io.inverno.mod.http.client.Exchange;
import io.inverno.mod.http.client.HttpClient;
import io.inverno.mod.http.client.HttpClientConfiguration;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p>
 * A sample HTTP service discovery capable HTTP client.
 * </p>
 *
 * <p>
 * It uses the two HTTP discovery service implementation exposed in the module: the {@code dnsHttpDiscoveryService} for resolving services identified by standard HTTP URIs
 * ({@code http://<host>:<port>} or {@code https://<host>:<port>})  and the {@code configurationHttpMetaDiscoveryService} for resolving services identified by configuration URIs
 * ({@code conf://<serviceName>}) pointing to service metadata defined in a configuration source.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean
public class DiscoveryHttpClient {

	private final HttpClient httpClient;
	private final List<HttpDiscoveryService> discoveryServices;
	private final ObjectMapper mapper;

	private final HttpTrafficPolicy trafficPolicy;

	public DiscoveryHttpClient(HttpClientConfiguration httpClientConfiguration, HttpClient httpClient, ObjectMapper mapper, List<HttpDiscoveryService> discoveryServices) {
		this.httpClient = httpClient;
		this.mapper = mapper;
		this.discoveryServices = discoveryServices;

		this.trafficPolicy = HttpTrafficPolicy.builder().configuration(httpClientConfiguration).build();
	}

	/**
	 * <p>
	 * Resolves the service from the specified {@code URI}, obtains a load balanced instance and send one or more {@code GET} request.
	 * </p>
	 *
	 * @param uri       the URI specifying the service to resolve and send the request target (path and query)
	 * @param count     the number of request to send
	 * @param authority the authority to set in the request
	 * @param headers   the headers to set in the request
	 *
	 * @return a stream of server responses
	 *
	 * @throws IllegalArgumentException if the specified count is not a positive integer
	 */
	public Flux<TestServerResponse> get(URI uri, int count, String authority, Map<String, String> headers) throws IllegalArgumentException {
		if(count < 1) {
			throw new IllegalArgumentException("Count must be a positive integer");
		}
		ServiceID serviceId = ServiceID.of(uri);
		String requestTarget = ServiceID.getRequestTarget(uri);
		return this.getDiscoveryService(serviceId)
			.resolve(serviceId, trafficPolicy)
			.switchIfEmpty(Mono.error(() -> new ServiceNotFoundException(serviceId)))
			.flatMapMany(service -> Flux.range(0, count)
				.flatMap(ign -> this.httpClient.exchange(requestTarget)
					.flatMap(exchange -> {
						if(StringUtils.isNotBlank(authority)) {
							exchange.request().authority(authority);
						}
						if(headers != null && !headers.isEmpty()) {
							exchange.request().headers(h -> headers.forEach(h::set));
						}
						return service.getInstance(exchange)
							.switchIfEmpty(Mono.error(() -> new ServiceNotFoundException(serviceId, "No matching instance")))
							.map(instance -> instance.bind(exchange));
					})
					.flatMap(Exchange::response)
					.flatMap(response -> Flux.from(response.body().string().stream()).collect(Collectors.joining())
						.map(responseBody -> {
							try {
								return this.mapper.readValue(responseBody, TestServerResponse.class);
							}
							catch (JsonProcessingException e) {
								throw Exceptions.propagate(e);
							}
						})
						.doOnNext(serverResponse -> {
							serverResponse.setResponseHeaders(
								response.headers().getAll().stream().collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.joining(", "))))
							);
						})
					)
				)
			);
	}

	/**
	 * <p>
	 * Resolves the HTTP discovery service to use based on the URI scheme.
	 * </p>
	 *
	 * <p>
	 * This is similar to what is done in the Web client which uses a caching composite discovery service.
	 * </p>
	 *
	 * @param serviceId the service ID exposing the service scheme
	 *
	 * @return an HTTP discovery service
	 *
	 * @throws IllegalArgumentException if no matching discovery service could be found
	 */
	private HttpDiscoveryService getDiscoveryService(ServiceID serviceId) throws IllegalArgumentException {
		for(HttpDiscoveryService discoveryService : this.discoveryServices) {
			if(discoveryService.supports(serviceId)) {
				return discoveryService;
			}
		}
		throw new IllegalArgumentException("No discovery service found resolving" + serviceId.getURI().getScheme());
	}
}
