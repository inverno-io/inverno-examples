package io.inverno.example.app_http_client.internal.command;

import io.inverno.mod.http.base.ExchangeContext;
import io.inverno.mod.http.base.Method;
import io.inverno.mod.http.client.Endpoint;
import io.inverno.mod.http.client.Exchange;
import picocli.CommandLine.Command;

@Command(
	name = "", 
	description = {
		"Http commands"
	}, 
	subcommands = {
		HttpCommands.HeadCommand.class, 
		HttpCommands.GetCommand.class, 
		HttpCommands.PatchCommand.class, 
		HttpCommands.PostCommand.class, 
		HttpCommands.PutCommand.class, 
		HttpCommands.DeleteCommand.class
	}
)
public class HttpCommands extends AbstractCommands {

	private Endpoint<ExchangeContext> endpoint;

	public void setEndpoint(Endpoint<ExchangeContext> endpoint) {
		this.endpoint = endpoint;
	}
	
	public Endpoint<ExchangeContext> getEndpoint() {
		return this.endpoint;
	}

	@Command(
		name = "get", 
		mixinStandardHelpOptions = true, 
		description = {"Send a GET HTTP request"})
	public static class GetCommand extends AbstractHttpCommand {

		public GetCommand() {
			super(Method.GET);
		}

		@Override
		protected void configure(Exchange<ExchangeContext> request) {
		}
	}

	@Command(
		name = "head", 
		mixinStandardHelpOptions = true, 
		description = {"Send a HEAD HTTP request"})
	public static class HeadCommand extends AbstractHttpCommand {

		public HeadCommand() {
			super(Method.HEAD);
		}

		@Override
		protected void configure(Exchange<ExchangeContext> request) {
		}
	}

	@Command(
		name = "patch", 
		mixinStandardHelpOptions = true, 
		description = {"Send a PATCH HTTP request"})
	public static class PatchCommand extends AbstractBodyHttpCommand {

		public PatchCommand() {
			super(Method.PATCH);
		}
	}

	@Command(
		name = "post", 
		mixinStandardHelpOptions = true, 
		description = {"Send a POST HTTP request"})
	public static class PostCommand extends AbstractBodyHttpCommand {

		public PostCommand() {
			super(Method.POST);
		}
	}

	@Command(
		name = "put", 
		mixinStandardHelpOptions = true, 
		description = {"Send a PUT HTTP request"})
	public static class PutCommand extends AbstractBodyHttpCommand {

		public PutCommand() {
			super(Method.PUT);
		}
	}

	@Command(
		name = "delete", 
		mixinStandardHelpOptions = true, 
		description = {"Send a DELETE HTTP request"})
	public static class DeleteCommand extends AbstractBodyHttpCommand {

		public DeleteCommand() {
			super(Method.DELETE);
		}
	}
}
