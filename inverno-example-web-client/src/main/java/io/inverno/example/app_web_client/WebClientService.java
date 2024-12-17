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

import io.inverno.core.annotation.Bean;
import io.inverno.mod.base.Charsets;
import io.inverno.mod.base.resource.FileResource;
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.base.HttpVersion;
import io.inverno.mod.http.base.InboundHeaders;
import io.inverno.mod.http.base.OutboundHeaders;
import io.inverno.mod.http.base.Status;
import io.inverno.mod.http.base.header.Headers;
import io.inverno.mod.http.client.ExchangeInterceptor;
import io.inverno.mod.http.client.InterceptedRequest;
import io.inverno.mod.http.client.InterceptedResponse;
import io.inverno.mod.http.client.Request;
import io.inverno.mod.web.client.InterceptedWebExchange;
import io.inverno.mod.web.client.WebClient;
import io.inverno.mod.web.client.WebExchange;
import java.io.File;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p>
 * The Web client service sending requests to HTTP services with the {@link WebClient}.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean
public class WebClientService {

	private final WebClient<? extends ExchangeContext> webClient;

	public WebClientService(WebClient<? extends ExchangeContext> webClient) {
		this.webClient = webClient.intercept().interceptor(new LoggerExchangeInterceptor<>());
	}

	public void executeCommand(WebClientCommand command) {
		this.createExchange(command)
			.flatMap(WebExchange::response)
			.flatMap(response -> {
				if(response.headers().getStatus().getCategory() == Status.Category.REDIRECTION) {
					return response.headers().get(Headers.NAME_LOCATION)
						.map(location -> this.createExchange(new WebClientCommand(command, location)).flatMap(WebExchange::response))
						.orElseGet(() -> Mono.just(response));
				}
				return Mono.just(response);
			})
			.flatMapMany(response -> response.body().raw().stream())
			.blockLast();
	}

	private Mono<? extends WebExchange<? extends ExchangeContext>> createExchange(WebClientCommand command) {
		return this.webClient.exchange(command.getMethod(), command.getURI())
			.doOnNext(exchange -> {
				exchange.failOnErrorStatus(false);
				command.getAuthority().ifPresent(exchange.request()::authority);
				exchange.request()
					.headers(h -> {
						command.getHeaders().forEach(h::add);
						command.getContentType().ifPresent(h::contentType);
						command.getAccept().ifPresent(h::accept);
						command.getAcceptEncoding().ifPresent(ae -> h.add(Headers.NAME_ACCEPT_ENCODING, ae));
						command.getAcceptLanguage().ifPresent(al -> h.add(Headers.NAME_ACCEPT_LANGUAGE, al));
					});

				if(command.getBody() != null) {
					if(command.getBody().getData() != null) {
						configureData(exchange.request(), command.getBody().getData());
					}
					else if(command.getBody().getUrlEncodedParams() != null && !command.getBody().getUrlEncodedParams().isEmpty()) {
						configureUrlEncoded(exchange.request(), command.getBody().getUrlEncodedParams());
					}
					else if(command.getBody().getMultipartParams() != null && !command.getBody().getMultipartParams().isEmpty()) {
						configureMultipart(exchange.request(), command.getBody().getMultipartParams());
					}
				}
			});
	}

	private static void configureData(Request request, String data) {
		if(data.startsWith("@")) {
			request.body().resource().value(new FileResource(new File(data.substring(1))));
		}
		else {
			request.body().string().value(data);
		}
	}

	private static void configureUrlEncoded(Request request, List<WebClientCommand.URlEncodedParameter> urlEncodedParams) {
		request.body().urlEncoded()
			.from((factory, data) -> data.stream(Flux.fromStream(urlEncodedParams.stream()
				.map(param -> factory.create(param.getName(), param.getValue()))
			)));
	}

	private static void configureMultipart(Request request, List<WebClientCommand.MultipartParameter> multipartParams) {
		request.body().multipart()
			.from((factory, data) -> data.stream(Flux.fromStream(multipartParams.stream()
				.map(param -> {
					if(param.getData().startsWith("@")) {
						return factory.resource(part -> part
							.name(param.getName())
							.filename(param.getFilename())
							.headers(h -> {
								param.getHeaders().forEach(h::add);
								param.getType().ifPresent(h::contentType);
							})
							.value(new FileResource(new File(param.getData().substring(1))))
						);
					}
					else {
						return factory.string(part -> part
							.name(param.getName())
							.filename(param.getFilename())
							.headers(h -> {
								param.getHeaders().forEach(h::add);
								param.getType().ifPresent(h::contentType);
							})
							.value(param.getData())
						);
					}
				})
			)));
	}

	/**
	 * <p>
	 * A Request/Response logger interceptor.
	 * </p>
	 */
	private static class LoggerExchangeInterceptor<A extends ExchangeContext, B extends InterceptedWebExchange<A>> implements ExchangeInterceptor<A, B> {

		private static final String LINE = "--------------------------------------------------------------------------------";

		@Override
		public Mono<B> intercept(B exchange) {
			final StringBuilder requestBodyBuilder = new StringBuilder();
			if(exchange.request().getMethod().isBodyAllowed()) {
				exchange.request().body().transform(data -> Flux.from(data)
					.doOnNext(buf -> requestBodyBuilder.append(buf.toString(Charsets.UTF_8)))
				);
			}

			final StringBuilder responseBodyBuilder = new StringBuilder();
			exchange.response().body()
				.transform(data -> Flux.from(data)
					.doOnNext(buf -> responseBodyBuilder.append(buf.toString(Charsets.UTF_8)))
					.doOnComplete(() -> {
						logRequest(exchange.request(), exchange.getProtocol(), requestBodyBuilder.toString());
						System.out.println(LINE);
						logResponse(exchange.response(), exchange.getProtocol(), responseBodyBuilder.toString());
					}));

			return Mono.just(exchange);
		}

		private static void logHeaders(InboundHeaders headers) {
			String direction = headers instanceof OutboundHeaders ? ">" : "<";
			headers.getAll().forEach(e -> System.out.printf("%s %s: %s%n", direction, e.getKey(), e.getValue()));
		}

		private static void logRequest(InterceptedRequest request, HttpVersion protocol, String body) {
			request.getRemoteCertificates().ifPresent(certificates -> {
				X509Certificate certificate = (X509Certificate)certificates[0];
				StringBuilder serverCert = new StringBuilder();

				serverCert
					.append("* Server certificate:\n")
					.append("   subject: ").append(certificate.getSubjectX500Principal().toString()).append("\n")
					.append("   start date: ").append(certificate.getNotBefore().toString()).append("\n")
					.append("   expire date: ").append(certificate.getNotAfter().toString()).append("\n")
					.append("   subjectAltName: ");

				try {
					serverCert.append(certificate.getSubjectAlternativeNames().stream().map(Object::toString).collect(Collectors.joining(", ")));
				}
				catch (CertificateParsingException ex) {
					// ign
				}
				serverCert.append("\n");

				serverCert.append("   issuer: ").append(certificate.getIssuerX500Principal().toString()).append("\n");

				System.out.println(serverCert);
			});

			System.out.printf("> %s %s %s%n", request.getMethod().name(), request.getPath(), protocol.getCode());

			logHeaders(request.headers());
			System.out.println();
			System.out.println(body);
		}

		private static void logResponse(InterceptedResponse response, HttpVersion protocol, String body) {
			System.out.printf("< %s %d %s%n", protocol.getCode(), response.headers().getStatus().getCode(), response.headers().getStatus().getReasonPhrase());

			logHeaders(response.headers());
			System.out.println();
			System.out.println(body);
		}
	}
}
