package io.inverno.example.app_http_client.internal.command;

import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.base.HttpVersion;
import io.inverno.mod.http.base.InboundHeaders;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.base.OutboundHeaders;
import io.inverno.mod.http.base.header.Headers;
import io.inverno.mod.http.client.Endpoint.Request;
import io.inverno.mod.http.client.Exchange;
import io.inverno.mod.http.client.InterceptableExchange;
import io.inverno.mod.http.client.Response;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Colors;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
		
		Request<ExchangeContext, Exchange<ExchangeContext>, InterceptableExchange<ExchangeContext>> request = this.httpCommands.getEndpoint().request(this.method, this.path)
			.headers(h -> {
				this.headers.forEach(h::add);
				this.accept.ifPresent(accept -> h.add(Headers.NAME_ACCEPT, accept));
				this.acceptEncoding.ifPresent(acceptEncoding -> h.add(Headers.NAME_ACCEPT_ENCODING, acceptEncoding));
				this.acceptLanguage.ifPresent(acceptLanguage -> h.add(Headers.NAME_ACCEPT_LANGUAGE, acceptLanguage));
			});
		
		this.configure(request);
		
		// Intercept request body
		final StringBuilder requestBodyBuilder = new StringBuilder();
		request.intercept(exchange -> {
			exchange.request().body()
				.ifPresent(body -> body.transform(data -> Flux.from(data)
					.doOnNext(buf -> requestBodyBuilder.append(buf.toString(StandardCharsets.UTF_8)))
				));
			return Mono.just(exchange);
		});

		try {
			char[] line = new char[Math.min(200, this.httpCommands.getTerminal().getWidth()) + 1];
			Arrays.fill(line, (char)0x2500);
			line[line.length - 1] = '\n';
			
			String body = request.send()
				.flatMapMany(exchange -> {
					this.logRequest(exchange.request(), exchange.getProtocol());
					if(!requestBodyBuilder.isEmpty()) {
						this.httpCommands.getTerminal().writer().println(requestBodyBuilder.toString());
					}
					this.httpCommands.getTerminal().writer().write(line);
					this.logResponse(exchange.response(), exchange.getProtocol());
					return exchange.response().body().string().stream();
				})
				.reduceWith(() -> new StringBuilder(), (acc, chunk) -> acc.append(chunk))
				.map(StringBuilder::toString)
				.block();

			if(!body.isEmpty()) {
				this.httpCommands.getTerminal().writer().println(body);
			}
		}
		catch(Exception e) {
			e.printStackTrace(this.httpCommands.getTerminal().writer());
		}
		finally {
			this.httpCommands.getTerminal().writer().flush();
		}
	}

	protected abstract void configure(Request<ExchangeContext, Exchange<ExchangeContext>, InterceptableExchange<ExchangeContext>> request);

	protected void logRequest(io.inverno.mod.http.client.Request request, HttpVersion protocol) {
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

			this.httpCommands.getTerminal().writer().println(new AttributedStringBuilder()
				.style(AttributedStyle.DEFAULT.foreground(Colors.rgbColor("grey50")))
				.append(serverCert.toString())
				.toAnsi()
			);
		});
	  
		this.httpCommands.getTerminal().writer().println(new AttributedStringBuilder()
			.style(AttributedStyle.BOLD.foreground(AttributedStyle.BLUE))
			.append(String.format("> %s %s %s", request.getMethod().name(), request.getPathAbsolute(), protocol.getCode()))
			.toAnsi()
		);
		
		this.logHeaders(request.headers());
		this.httpCommands.getTerminal().writer().println();
	}

	protected void logResponse(Response response, HttpVersion protocol) {
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
		this.httpCommands.getTerminal().writer().println(new AttributedStringBuilder()
			.style(AttributedStyle.BOLD.foreground(color))
			.append(String.format("< %s %d %s", protocol.getCode(), response.headers().getStatus().getCode(), response.headers().getStatus().getReasonPhrase()))
			.toAnsi()
		);
		this.logHeaders(response.headers());
		this.httpCommands.getTerminal().writer().println();
	}

	protected void logHeaders(InboundHeaders headers) {
		String direction = headers instanceof OutboundHeaders ? ">" : "<";
		headers.getAll().forEach(e -> this.httpCommands.getTerminal().writer().println(new AttributedStringBuilder()
				.style(AttributedStyle.DEFAULT.foreground(Colors.rgbColor("grey50")))
				.append(String.format("%s %s: %s", direction, e.getKey(), e.getValue()))
				.toAnsi()
			)
		);
	}
}
