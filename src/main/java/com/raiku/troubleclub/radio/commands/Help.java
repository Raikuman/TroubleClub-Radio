package com.raiku.troubleclub.radio.commands;

import com.raikuman.botutilities.buttons.pagination.manager.PageCommandInterface;
import com.raikuman.botutilities.buttons.pagination.manager.Pagination;
import com.raikuman.botutilities.buttons.pagination.manager.PaginationBuilder;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.botutilities.context.EventContext;
import net.dv8tion.jda.api.interactions.components.ItemComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles sending a pagination of commands on the bot
 *
 * @version 1.0 2020-23-06
 * @since 1.0
 */
public class Help implements CommandInterface, PageCommandInterface {

	private final List<CommandInterface> commands;

	public Help(List<CommandInterface> commands) {
		this.commands = commands;
	}

	@Override
	public void handle(CommandContext ctx) {
		PaginationBuilder paginationBuilder = new PaginationBuilder(
			ctx.getEventMember(),
			getInvoke(),
			pageStrings(ctx),
			itemsPerPage(),
			loopPagination()
		);

		Pagination pagination = paginationBuilder.build();
		List<ItemComponent> componentList = Arrays.asList(
			pagination.provideLeft(),
			pagination.provideFirst(),
			pagination.provideRight()
		);

		ctx.getChannel().sendMessageEmbeds(
			paginationBuilder.buildEmbeds().get(0).build()
		).setActionRow(componentList).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "help";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Shows all music commands";
	}

	@Override
	public List<String> pageStrings(EventContext eventContext) {
		List<String> stringList = new ArrayList<>();
		StringBuilder builder = new StringBuilder();

		int commandsPerPage = 10;
		List<String> commandStrings = buildCommandStrings();
		int numPages = commandStrings.size() / commandsPerPage;
		if ((commandStrings.size() % commandsPerPage) > 0)
			numPages++;

		int currentCommand = 0;
		for (int i = 0; i < numPages; i++) {
			builder.append("```asciidoc\n");

			for (int j = 0; j < commandsPerPage; j++) {
				builder
					.append(commandStrings.get(currentCommand))
					.append("\n\n");

				currentCommand++;
			}

			builder.append("```");
			stringList.add(builder.toString());
			builder = new StringBuilder();
		}

		return stringList;
	}

	@Override
	public int itemsPerPage() {
		return 1;
	}

	@Override
	public boolean loopPagination() {
		return true;
	}

	@Override
	public boolean addHomeBtn() {
		return false;
	}

	@Override
	public boolean addFirstPageBtn() {
		return true;
	}

	/**
	 * Builds a list of strings based on information from commands
	 * @return The list of command strings
	 */
	private List<String> buildCommandStrings() {
		List<String> commandStrings = new ArrayList<>();

		StringBuilder aliases = new StringBuilder();
		StringBuilder commandInfo = new StringBuilder();
		for (CommandInterface command : commands) {
			if (!command.getAliases().isEmpty()) {
				aliases
					.append("(")
					.append(String.join(", ", command.getAliases()))
					.append(")");
			}

			commandInfo
				.append(ConfigIO.readConfig("settings", "prefix"))
				.append(command.getInvoke())
				.append(" ");

			if (aliases.length() > 0)
				commandInfo
					.append(aliases)
					.append(" ");

			if (!command.getUsage().isEmpty())
				commandInfo
					.append(command.getUsage())
					.append(" ");

			commandInfo
				.append(":: ")
				.append(command.getDescription());

			commandStrings.add(commandInfo.toString());
			aliases = new StringBuilder();
			commandInfo = new StringBuilder();
		}

		return commandStrings;
	}
}
