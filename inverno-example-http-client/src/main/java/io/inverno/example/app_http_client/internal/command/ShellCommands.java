package io.inverno.example.app_http_client.internal.command;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
	name = "", 
	description = {
		"Shell commands"
	}, 
	subcommands = {
		ShellCommands.ClearCommand.class 
	}
)
public class ShellCommands implements Runnable {

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
	
	@Command(
		name = "clear", 
		mixinStandardHelpOptions = true, 
		description = {"Clear screen"})
	public static class ClearCommand implements Runnable {

		@ParentCommand
		private ShellCommands shellCommands;
		
		@Override
		public void run() {
			this.shellCommands.getTerminal().puts(Capability.clear_screen);
		}
	}
}
