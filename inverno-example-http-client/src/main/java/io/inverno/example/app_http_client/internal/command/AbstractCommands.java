package io.inverno.example.app_http_client.internal.command;

import org.jline.terminal.Terminal;
import picocli.CommandLine;

public abstract class AbstractCommands implements Runnable {

	private Terminal terminal;
	
	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}

	public Terminal getTerminal() {
		return this.terminal;
	}

	@Override
	public void run() {
		this.terminal.writer().println(new CommandLine(this).getUsageMessage());
	}
}
