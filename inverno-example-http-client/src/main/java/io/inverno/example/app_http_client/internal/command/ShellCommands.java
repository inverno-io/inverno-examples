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

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

/**
 * <p>
 *
 * </p>
 *
 * @author <a href="jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
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
