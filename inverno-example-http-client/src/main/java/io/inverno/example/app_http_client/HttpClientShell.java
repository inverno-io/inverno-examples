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
package io.inverno.example.app_http_client;

import io.inverno.core.annotation.Bean;
import io.inverno.core.annotation.Bean.Visibility;
import io.inverno.core.annotation.Init;
import io.inverno.example.app_http_client.internal.command.HttpClientCommands;
import io.inverno.example.app_http_client.internal.command.HttpCommands;
import io.inverno.example.app_http_client.internal.command.ShellCommands;
import io.inverno.example.app_http_client.internal.picocli.PicocliCommandRegistry;
import io.inverno.mod.configuration.ConfigurationSource;
import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.client.Endpoint;
import io.inverno.mod.http.client.HttpClient;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Supplier;
import org.jline.builtins.ConfigurationPath;
import org.jline.console.SystemRegistry;
import org.jline.console.impl.Builtins;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.reader.EndOfFileException;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Parser;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultHighlighter;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.widget.AutosuggestionWidgets;
import org.jline.widget.TailTipWidgets;
import picocli.CommandLine;

/**
 * <p>
 *
 * </p>
 *
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@Bean(visibility = Visibility.PUBLIC)
public class HttpClientShell extends Thread implements Runnable {

	private final HttpClient client;

	private ConfigurationSource configurationSource;
	
	public HttpClientShell(HttpClient client, ConfigurationSource configurationSource) {
		this.client = client;
		this.configurationSource = configurationSource;
	}
	
	@Init
	@Override
	public void start() {
		super.start();
	}
	
	@Override
	public void run() {
		try(Terminal terminal = TerminalBuilder.builder()
			.jna(true)
			.color(true)
			.build()) {
			
			Supplier<Path> workDir = () -> Paths.get(System.getProperty("user.dir"));
			ConfigurationPath configPath = new ConfigurationPath(null, null);

			Builtins builtins = new Builtins(workDir, configPath, ign -> null);
			HttpClientCommands httpClientCommands = new HttpClientCommands(this.client, this.configurationSource);
			HttpCommands httpCommands = new HttpCommands();
			ShellCommands shellCommands = new ShellCommands();
			
			Parser parser = new DefaultParser();
			Highlighter highlighter = new DefaultHighlighter();
			SystemRegistry systemRegistry = new SystemRegistryImpl(parser, terminal, workDir, configPath);
			systemRegistry.setCommandRegistries(
				builtins, 
				new PicocliCommandRegistry(new CommandLine(shellCommands), "Shell commands"),
				new PicocliCommandRegistry(new CommandLine(httpClientCommands), "Client commands"),
				new PicocliCommandRegistry(new CommandLine(httpCommands), "Http commands")
			);
			
			LineReader lineReader = LineReaderBuilder.builder()
				.terminal(terminal)
				.completer(systemRegistry.completer())
				.highlighter(highlighter)
				.parser(parser)
				.build();
			
			builtins.setLineReader(lineReader);
			httpCommands.setTerminal(terminal);
			httpClientCommands.setTerminal(terminal);
			shellCommands.setTerminal(terminal);
			
			AutosuggestionWidgets autosuggestionWidgets = new AutosuggestionWidgets(lineReader);
			autosuggestionWidgets.enable();
			
			TailTipWidgets tailTipWidgets = new TailTipWidgets(lineReader, systemRegistry::commandDescription, 5, TailTipWidgets.TipType.COMPLETER);
			tailTipWidgets.enable();

			String line;
			while (true) {
				try {
					systemRegistry.cleanUp();
					line = lineReader.readLine(this.getPrompt(httpClientCommands.getEndpoint()));
					systemRegistry.execute(line);
				} 
				catch (UserInterruptException e) {
					// Ignore
				} 
				catch (EndOfFileException e) {
					return;
				} 
				catch (Exception e) {
					systemRegistry.trace(e);
				}
				finally {
					httpCommands.setEndpoint(httpClientCommands.getEndpoint().orElse(null));
				}
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getPrompt(Optional<Endpoint<ExchangeContext>> endpoint) {
		AttributedStringBuilder builder = new AttributedStringBuilder()
			.style(AttributedStyle.BOLD);
		
		endpoint
			.map(Endpoint::getRemoteAddress)
			.ifPresent(address -> builder.append(((InetSocketAddress)address).getHostName() + ":" + ((InetSocketAddress)address).getPort()));

		return builder.append("> ").toAnsi();
	}
}
