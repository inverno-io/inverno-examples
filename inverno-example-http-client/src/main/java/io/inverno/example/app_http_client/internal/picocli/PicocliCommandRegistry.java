package io.inverno.example.app_http_client.internal.picocli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jline.builtins.Options;
import org.jline.console.ArgDesc;
import org.jline.console.CmdDesc;
import org.jline.console.CommandMethods;
import org.jline.console.impl.AbstractCommandRegistry;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedString;

import picocli.CommandLine;

public class PicocliCommandRegistry extends AbstractCommandRegistry {

	private final CommandLine commandLine;
	
	private String name;

	public PicocliCommandRegistry(CommandLine commandLine) {
		this(commandLine, null);
	}

	public PicocliCommandRegistry(CommandLine commandLine, String name) {
		this.commandLine = commandLine;
		this.name = name;
		final Map<String, CommandMethods> commands = new HashMap<>();
		this.commandLine.getCommandSpec().subcommands()
			.forEach( (commandName, command) -> {
				List<Completer> commandCompleters = List.of(new PicocliCommandCompleter(command));
				List<String> allCmdNames = new ArrayList<>();
				allCmdNames.add(commandName);
				Arrays.stream(command.getCommandSpec().aliases()).forEach(allCmdNames::add);
				
				allCmdNames.forEach(cmdName -> commands.put(
				commandName, 
					new CommandMethods(input -> {
							List<String> cmdArgs = new ArrayList<>();
							cmdArgs.add(commandName);
							cmdArgs.addAll(Arrays.asList(input.args()));
							this.commandLine.execute(cmdArgs.toArray(String[]::new));
						},
						ign -> commandCompleters
					)
				));
			});
		this.registerCommands(commands);
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String name() {
		return this.name != null ? this.name : super.name();
	}
	
	@Override
	public List<String> commandInfo(String command) {
		CommandLine.Model.CommandSpec commandSpec = this.commandLine.getSubcommands().get(command).getCommandSpec();
		CommandLine.Help commandHelp = new CommandLine.Help(commandSpec);
		String description = AttributedString.stripAnsi(commandSpec.usageMessage().sectionMap().get("description").render(commandHelp).toString());
		return Arrays.asList(description.split("\\r?\\n"));
	}

	@Override
	public CmdDesc commandDescription(List<String> args) {
		CommandLine command = this.findCommand(args, args.size());
		if(command != null) {
			CommandLine.Model.CommandSpec commandSpec = command.getCommandSpec();
			CommandLine.Help commandHelp = new CommandLine.Help(commandSpec);
			
			List<AttributedString> main = new ArrayList<>();
			String synopsis = AttributedString.stripAnsi(commandSpec.usageMessage().sectionMap().get("synopsis").render(commandHelp).toString());
			main.add(Options.HelpException.highlightSyntax(synopsis.trim(), Options.HelpException.defaultStyle()));
			
			List<ArgDesc> arguments = new ArrayList<>();
			if(args.size() == 1) {
				arguments.add(new ArgDesc(""));
			}
			else {
				for(CommandLine.Model.PositionalParamSpec param : commandSpec.positionalParameters()) {
					if (!param.hidden()) {
						String argumentName = param.paramLabel();
						List<AttributedString> argumentDescription = new ArrayList<>();
						for(String paramDesc : param.description()) {
							argumentDescription.add(new AttributedString(paramDesc));
						}
						arguments.add(new ArgDesc(argumentName, argumentDescription));
					}
				}
			}
			
			Map<String, List<AttributedString>> options = new HashMap<>();
			for(CommandLine.Model.OptionSpec opt : commandSpec.options()) {
				String optionName = Arrays.stream(opt.names()).collect(Collectors.joining(" "));
				List<AttributedString> optionDescription = new ArrayList<>();
				for(String optDesc : opt.description()) {
					optionDescription.add(new AttributedString(optDesc));
				}
				if(opt.arity().max() > 0) {
					optionName += "=" + opt.paramLabel();
				}
				options.put(optionName, optionDescription);
			}
			return new CmdDesc(main, arguments, options);
		}
		return null;
	}
	
	public CommandLine findCommand(List<String> words, int currentIndex) {
		CommandLine cmd = this.commandLine;
		int i = 0;
		while(cmd != null && i < currentIndex) {
			String subCommandName = words.get(i);
			if(!isOption(words.get(i))) {
				cmd = this.findSubCommand(cmd, subCommandName);
			}
			i++;
		}
		return cmd;
	}
	
	public CommandLine findSubCommand(CommandLine parentCommand, String subCommandName) {
		for(CommandLine subCommand : parentCommand.getSubcommands().values()) {
			if(subCommandName.equals(subCommand.getCommandName()) || Arrays.stream(subCommand.getCommandSpec().aliases()).anyMatch(subCommandName::equals)) {
				return subCommand;
			}
		}
		return null;
	}
	
	private static boolean isOption(String word) {
		return word.startsWith("-");
	}
	
	private static class PicocliCommandCompleter implements Completer {

		private final CommandLine command;

		public PicocliCommandCompleter(CommandLine command) {
			this.command = command;
		}

		@Override
		public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
			if(isOption(line.word())) {
				String wordAtCursor = line.word().substring(0, line.wordCursor());
				int eqIndex = wordAtCursor.indexOf("=");
				for(CommandLine.Model.OptionSpec option : this.command.getCommandSpec().options()) {
					if(option.arity().max() == 0 && eqIndex < 0) {
						for(String optionName : option.names()) {
							candidates.add(createCandidate(optionName));
						}
					}
					else if(eqIndex > 0) {
						String optionName = wordAtCursor.substring(0, eqIndex);
						if(Arrays.stream(option.names()).anyMatch(optionName::equals) && option.completionCandidates() != null) {
							String optionLeftHandSide = optionName + "=";
							for(String completionCandidate : option.completionCandidates()) {
								candidates.add(createCandidate(completionCandidate, optionLeftHandSide, "", false));
							}
						}
					}
					else {
						for(String optionName : option.names()) {
							candidates.add(createCandidate(optionName, "", "=", false));
						}
					}
				}
			}
			else {
				for(CommandLine subCommand : this.command.getSubcommands().values()) {
					candidates.add(new Candidate(AttributedString.stripAnsi(subCommand.getCommandName()), subCommand.getCommandName(), null, null, null, null, true));
					for(String alias : subCommand.getCommandSpec().aliases()) {
						candidates.add(createCandidate(alias));
					}
				}
			}
		}

		private static Candidate createCandidate(String value) {
			return new Candidate(AttributedString.stripAnsi(value), value, null, null, null, null, true);
		}

		private static Candidate createCandidate(String value, String prefix, String postFix, boolean complete) {
			return new Candidate(AttributedString.stripAnsi(prefix + value + postFix), value, null, null, null, null, complete);
		}
	}
}
