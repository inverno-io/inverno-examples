package io.inverno.example.app_http_client.internal.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Colors;

import io.inverno.example.app_http_client.App_http_clientConfigurationLoader;
import io.inverno.mod.base.Charsets;
import io.inverno.mod.configuration.ConfigurationSource;
import io.inverno.mod.configuration.source.CompositeConfigurationSource;
import io.inverno.mod.configuration.source.PropertiesConfigurationSource;
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.base.HttpVersion;
import io.inverno.mod.http.base.InboundHeaders;
import io.inverno.mod.http.base.OutboundHeaders;
import io.inverno.mod.http.client.Endpoint;
import io.inverno.mod.http.client.HttpClient;
import io.inverno.mod.http.client.HttpClientConfiguration;
import io.inverno.mod.http.client.HttpClientConfigurationLoader;
import io.inverno.mod.http.client.InterceptableRequest;
import io.inverno.mod.http.client.InterceptableResponse;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Command(
	name = "", 
	description = {
		"Endpoint commands"
	}, 
	subcommands = {
		HttpClientCommands.OpenCommand.class, 
		HttpClientCommands.CloseCommand.class,
		HttpClientCommands.ConfigurationCommand.class
	}
)
public class HttpClientCommands extends AbstractCommands {
	
	private static final String HTTP_CLIENT_CONFIGURATION_PREFIX = "io.inverno.example.app_http_client.app_http_clientConfiguration.http_client.";
	
	private final HttpClient httpClient;

	private ConfigurationSource<?, ?, ?> configurationSource;
	
	private final App_http_clientConfigurationLoader configurationLoader;

	private final Properties configurationProperties;
	
	private Endpoint<ExchangeContext> endpoint;
	
	public HttpClientCommands(HttpClient httpClient, ConfigurationSource<?, ?, ?> configurationSource) {
		this.httpClient = httpClient;
		this.configurationProperties = new Properties();
		this.configurationSource = new CompositeConfigurationSource(List.of(new PropertiesConfigurationSource(this.configurationProperties), configurationSource));
		this.configurationLoader = new App_http_clientConfigurationLoader();
		this.configurationLoader.withSource(this.configurationSource);
	}
	
	protected ConfigurationSource<?, ?, ?> getConfigurationSource() {
		return this.configurationSource;
	}
	
	protected Properties getConfigurationProperties() {
		return this.configurationProperties;
	}
	
	protected HttpClientConfiguration getHttpClientConfiguration() {
		return this.configurationLoader.load().block().http_client();
	}
	
	protected HttpClientConfiguration getHttpClientConfiguration(Consumer<HttpClientConfigurationLoader.Configurator> configurer) {
		return HttpClientConfigurationLoader.load(this.getHttpClientConfiguration(), configurer);
	}
	
	protected HttpClient getHttpClient() {
		return this.httpClient;
	}
	
	public Optional<Endpoint<ExchangeContext>> getEndpoint() {
		return Optional.ofNullable(this.endpoint);
	}
	
	protected void setEndpoint(Endpoint<ExchangeContext> endpoint) {
		this.endpoint = endpoint;
	}
	
	@Command(
		name = "config", 
		mixinStandardHelpOptions = true, 
		description = {"Manage Http Client configuration properties"},
		subcommands = {CommandLine.HelpCommand.class}
	)
	public static class ConfigurationCommand implements Runnable {

		@ParentCommand
		protected HttpClientCommands httpClientCommands;
		
		@Override
		public void run() {
			this.httpClientCommands.getTerminal().writer().println(new CommandLine(this).getUsageMessage());
		}
		
		@Command(
			name = "list",
			description = "List Http Client configuration properties",
			mixinStandardHelpOptions = true 
		)
		public void list() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			HttpClientConfiguration httpClientConfiguration = this.httpClientCommands.getHttpClientConfiguration();
			for(Method accessor : HttpClientConfiguration.class.getMethods()) {
				if(accessor.getParameterCount() == 0) {
					this.httpClientCommands.getTerminal().writer().println(accessor.getName() + "=" + accessor.invoke(httpClientConfiguration));
				}
			}
		}
		
		@Command(
			name = "get",
			description = "Get an Http Client configuration property",
			mixinStandardHelpOptions = true 
		)
		public void get(@Parameters(index = "0", paramLabel = "name", description = "Property name") String propertyName) throws SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			try {
				this.httpClientCommands.getTerminal().writer().println(HttpClientConfiguration.class.getMethod(propertyName).invoke(this.httpClientCommands.getHttpClientConfiguration()));
			}
			catch (NoSuchMethodException e) {
				this.httpClientCommands.getTerminal().writer().println("Unknown property: " + propertyName);
			}
			catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace(this.httpClientCommands.getTerminal().writer());
			}
		}
		
		@Command(
			name = "set",
			description = "Set an Http Client configuration property",
			mixinStandardHelpOptions = true 
		)
		public void set(@Parameters(index = "0", paramLabel = "name", description = "Property name") String propertyName, @Parameters(index = "1", paramLabel = "value", description = "Property value") String propertyValue) throws SecurityException {
			try {
				HttpClientConfiguration.class.getMethod(propertyName);
			} 
			catch (NoSuchMethodException e) {
				this.httpClientCommands.getTerminal().writer().println("Unknown property: " + propertyName);
				return;
			}
			this.httpClientCommands.getConfigurationProperties().setProperty(HTTP_CLIENT_CONFIGURATION_PREFIX + propertyName, propertyValue);
		}
	}
	
	@Command(
		name = "open",
		description = "Connect to an Http endpoint",
		mixinStandardHelpOptions = true 
	)
	public static class OpenCommand implements Runnable {
		
		@Option(
			names = {"-s", "--tls-enabled"}, 
			description = "Enable TLS",
			required = false
		)
		private boolean tls_enabled;
		
		@Option(
			names = {"-t", "--tls-trust-all"}, 
			description = "Trust all certificates (insecure)",
			required = false
		)
		private boolean tls_trust_all;
		
		@Option(
			names = {"-p", "--protocol"},
			description = "HTTP protocol version to use",
			required = false
		)
		private Set<HttpVersion> httpVersions;
		
		@Parameters(
			index = "0",
			paramLabel = "host",
			description = {"Endpoint host"}
		)
		private String host;
		
		@Parameters(
			index = "1",
			paramLabel = "port",
			description = {"Endpoint port"},
			arity = "0..1"
		)
		private Optional<Integer> port;
		
		@ParentCommand
		protected HttpClientCommands httpClientCommands;
		
		public char[] getLine() {
			char[] line = new char[Math.min(200, this.httpClientCommands.getTerminal().getWidth()) + 1];
			Arrays.fill(line, (char)0x2500);
			line[line.length - 1] = '\n';
			return line;
		}

		@Override
		public void run() {
			this.httpClientCommands.getEndpoint().ifPresentOrElse(
				endpoint -> {
					throw new IllegalStateException("Endpoint " + endpoint.getRemoteAddress() + " already connected");
				}, 
				() -> {
					Endpoint<ExchangeContext> endpoint = this.httpClientCommands.getHttpClient()
						.endpoint(this.host, this.port.orElseGet(() -> this.tls_enabled ? 443 : 80))
						.configuration(this.httpClientCommands.getHttpClientConfiguration(config -> { 
							config
								.tls_enabled(this.tls_enabled)
								.tls_trust_all(this.tls_trust_all);
							if(this.httpVersions != null && !this.httpVersions.isEmpty()) {
								config.http_protocol_versions(this.httpVersions);
							}
						}))
						.interceptor(exchange -> {
							final StringBuilder requestBodyBuilder = new StringBuilder();
							exchange.request().body().ifPresent(body -> body
								.transform(data -> Flux.from(data)
									.doOnNext(buf -> requestBodyBuilder.append(buf.toString(Charsets.UTF_8)))
								));
							
							final StringBuilder responseBodyBuilder = new StringBuilder();
							exchange.response().body()
								.transform(data -> Flux.from(data)
									.doOnNext(buf -> responseBodyBuilder.append(buf.toString(Charsets.UTF_8)))
									.doOnComplete(() -> {
										this.logRequest(exchange.request(), exchange.getProtocol(), requestBodyBuilder.toString());
										this.httpClientCommands.getTerminal().writer().write(this.getLine());
										this.logResponse(exchange.response(), exchange.getProtocol(), responseBodyBuilder.toString());
									}));
							
							return Mono.just(exchange);
						})
						.build();
					this.httpClientCommands.setEndpoint(endpoint);
					this.httpClientCommands.getTerminal().writer().println("Connected to " + endpoint.getRemoteAddress());
				}
			);
		}
		
		private void logHeaders(InboundHeaders headers) {
			String direction = headers instanceof OutboundHeaders ? ">" : "<";
			headers.getAll().forEach(e -> this.httpClientCommands.getTerminal().writer().println(new AttributedStringBuilder()
					.style(AttributedStyle.DEFAULT.foreground(Colors.rgbColor("grey50")))
					.append(String.format("%s %s: %s", direction, e.getKey(), e.getValue()))
					.toAnsi()
				)
			);
		}
		
		private void logRequest(InterceptableRequest request, HttpVersion protocol, String body) {
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

				this.httpClientCommands.getTerminal().writer().println(new AttributedStringBuilder()
					.style(AttributedStyle.DEFAULT.foreground(Colors.rgbColor("grey50")))
					.append(serverCert.toString())
					.toAnsi()
				);
			});
		  
			this.httpClientCommands.getTerminal().writer().println(new AttributedStringBuilder()
				.style(AttributedStyle.BOLD.foreground(AttributedStyle.BLUE))
				.append(String.format("> %s %s %s", request.getMethod().name(), request.getPathAbsolute(), protocol.getCode()))
				.toAnsi()
			);
			
			this.logHeaders(request.headers());
			this.httpClientCommands.getTerminal().writer().println();
			this.httpClientCommands.getTerminal().writer().println(body);
		}
		
		private void logResponse(InterceptableResponse response, HttpVersion protocol, String body) {
			int color;
			switch(response.headers().getStatus().getCategory()) {
				case INFORMATIONAL: color = AttributedStyle.CYAN;
					break;
				case SUCCESSUL: color = AttributedStyle.GREEN;
					break;
				case REDIRECTION: color = AttributedStyle.YELLOW;
					break;
				case CLIENT_ERROR:
				case SERVER_ERROR: color = AttributedStyle.RED;
					break;
				default: color = Colors.rgbColor("grey30");
			}
			this.httpClientCommands.getTerminal().writer().println(new AttributedStringBuilder()
				.style(AttributedStyle.BOLD.foreground(color))
				.append(String.format("< %s %d %s", protocol.getCode(), response.headers().getStatus().getCode(), response.headers().getStatus().getReasonPhrase()))
				.toAnsi()
			);
			this.logHeaders(response.headers());
			this.httpClientCommands.getTerminal().writer().println();
			this.httpClientCommands.getTerminal().writer().println(body);
		}
	}
	
	@Command(
		name = "close",
		description = "Close the Http endpoint",
		mixinStandardHelpOptions = true 
	)
	public static class CloseCommand implements Runnable {

		@ParentCommand
		protected HttpClientCommands httpClientCommands;
		
		@Override
		public void run() {
			this.httpClientCommands.getEndpoint().ifPresentOrElse(
				endpoint -> {
					endpoint.shutdown().block();
					this.httpClientCommands.setEndpoint(null);
					this.httpClientCommands.getTerminal().writer().println("Disconnected");
				},
				() -> this.httpClientCommands.getTerminal().writer().println("No connected endpoint"));
		}
	}
}
