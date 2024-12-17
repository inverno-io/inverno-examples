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
package io.inverno.example.app_web_client.book;

import io.inverno.example.app_web_client.book.dto.Book;
import io.inverno.mod.http.base.NotFoundException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

/**
 * <p>
 * Book client command line.
 * </p>
 *
 * @author <a href="mailto:jeremy.kuhn@inverno.io">Jeremy Kuhn</a>
 */
@CommandLine.Command(
	name = "",
	description = {
		"Inverno Book resource commands"
	},
	subcommands = {
		CommandLine.HelpCommand.class,
		BookCommands.CreateCommand.class,
		BookCommands.UpdateCommand.class,
		BookCommands.ListCommand.class,
		BookCommands.GetCommand.class,
		BookCommands.DeleteCommand.class
	}
)
public class BookCommands {

	private final BookClient bookClient;

	public BookCommands(BookClient bookClient) {
		this.bookClient = bookClient;
	}

	@CommandLine.Command(
		name = "create",
		mixinStandardHelpOptions = true,
		description = {"Creates a book resource"})
	public static class CreateCommand implements Runnable {

		@CommandLine.ParentCommand
		protected BookCommands bookCommands;

		@CommandLine.Option(
			names = {"--isbn"},
			description = "Book ISBN",
			required = true
		)
		private String isbn;

		@CommandLine.Option(
			names = {"--title"},
			description = "Book Title",
			required = true
		)
		private String title;

		@CommandLine.Option(
			names = {"--author"},
			description = "Book Author",
			required = true
		)
		private String author;

		@CommandLine.Option(
			names = {"--pages"},
			description = "Book number of pages",
			required = true
		)
		private int pages;

		@Override
		public void run() {
			this.bookCommands.bookClient.create(new Book(this.isbn, this.title, this.author, this.pages)).block();
			System.out.println("1 book created");
		}
	}

	@CommandLine.Command(
		name = "update",
		mixinStandardHelpOptions = true,
		description = {"Updates a book resource"})
	public static class UpdateCommand implements Runnable {

		@CommandLine.ParentCommand
		protected BookCommands bookCommands;

		@CommandLine.Option(
			names = {"--isbn"},
			description = "Book ISBN",
			required = true
		)
		private String isbn;

		@CommandLine.Option(
			names = {"--title"},
			description = "Book Title",
			required = true
		)
		private String title;

		@CommandLine.Option(
			names = {"--author"},
			description = "Book Author",
			required = true
		)
		private String author;

		@CommandLine.Option(
			names = {"--pages"},
			description = "Book number of pages",
			required = true
		)
		private int pages;

		@Override
		public void run() {
			try {
				this.bookCommands.bookClient.update(this.isbn, new Book(this.isbn, this.title, this.author, this.pages)).block();
				System.out.println("1 book updated");
			}
			catch(NotFoundException e) {
				System.out.println("No book was found");
			}
		}
	}

	@CommandLine.Command(
		name = "list",
		mixinStandardHelpOptions = true,
		description = {"List book resources"})
	public static class ListCommand implements Runnable {

		@CommandLine.ParentCommand
		protected BookCommands bookCommands;

		@Override
		public void run() {
			List<Book> list = this.bookCommands.bookClient.list().collectList().block();
			System.out.println(list.size() + " book(s)");

			list.stream().map(book -> " * " + book).forEach(System.out::println);
		}
	}

	@CommandLine.Command(
		name = "get",
		mixinStandardHelpOptions = true,
		description = {"Get a book resource"})
	public static class GetCommand implements Runnable {

		@CommandLine.ParentCommand
		protected BookCommands bookCommands;

		@CommandLine.Parameters(
			index= "0",
			paramLabel = "isbn",
			description = "Book ISBN"
		)
		private String isbn;

		@Override
		public void run() {
			try {
				Book book = this.bookCommands.bookClient.get(this.isbn).block();
				System.out.println(book);
			}
			catch(NotFoundException e) {
				System.out.println("No book was found");
			}
		}
	}

	@CommandLine.Command(
		name = "delete",
		mixinStandardHelpOptions = true,
		description = {"Delete a book resource"})
	public static class DeleteCommand implements Runnable {

		@CommandLine.ParentCommand
		protected BookCommands bookCommands;

		@CommandLine.Parameters(
			index= "0",
			paramLabel = "isbn",
			description = "Book ISBN"
		)
		private String isbn;

		@Override
		public void run() {
			try {
				this.bookCommands.bookClient.delete(this.isbn).block();
				System.out.println("1 book deleted");
			}
			catch(NotFoundException e) {
				System.out.println("No book was found");
			}
		}
	}
}
