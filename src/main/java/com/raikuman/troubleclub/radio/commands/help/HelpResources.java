package com.raikuman.troubleclub.radio.commands.help;

import com.raikuman.botutilities.buttons.pagination.manager.Pagination;
import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.configs.Prefix;
import com.raikuman.botutilities.context.EventContext;
import com.raikuman.troubleclub.radio.category.MusicCategory;
import com.raikuman.troubleclub.radio.category.OtherCategory;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.category.TrackCategory;
import com.raikuman.troubleclub.radio.commands.help.selects.MusicSelect;
import com.raikuman.troubleclub.radio.commands.help.selects.OtherSelect;
import com.raikuman.troubleclub.radio.commands.help.selects.PlaylistSelect;
import com.raikuman.troubleclub.radio.commands.help.selects.TrackSelect;
import com.raikuman.troubleclub.radio.commands.music.Play;
import com.raikuman.troubleclub.radio.commands.music.SkipTo;
import com.raikuman.troubleclub.radio.commands.music.Stop;
import com.raikuman.troubleclub.radio.listener.handler.InvokeInterfaceProvider;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class with helpful methods for the help command
 *
 * @version 1.4 2023-11-01
 * @since 1.1
 */
public class HelpResources {

	/**
	 * returns a pagination object with information from the help command
	 * @param ctx The event context to get the event member from
	 * @return The pagination object
	 */
	public static Pagination getHomePagination(EventContext ctx) {
		Help help = new Help();
		return new Pagination(
			ctx.getEventMember(),
			help.getInvoke(),
			help.pageName(),
			help.pageStrings(ctx),
			help.itemsPerPage(),
			help.loopPagination()
		);
	}

	/**
	 * Returns all required action rows for the help command
	 * @param ctx The event context to get the event member from
	 * @return The list of action rows
	 */
	public static List<ActionRow> getHomeActionRows(EventContext ctx) {
		Pagination pagination = getHomePagination(ctx);

		List<ItemComponent> componentList = Arrays.asList(
			pagination.provideLeft(),
			pagination.provideRight()
		);

		final MusicSelect musicSelect = new MusicSelect(InvokeInterfaceProvider.provideCommands());
		final TrackSelect trackSelect = new TrackSelect(InvokeInterfaceProvider.provideCommands());
		final PlaylistSelect playlistSelect = new PlaylistSelect(InvokeInterfaceProvider.provideCommands());
		final OtherSelect otherSelect = new OtherSelect(InvokeInterfaceProvider.provideCommands());

		StringSelectMenu selectMenu =
			StringSelectMenu.create(ctx.getEventMember().getId() + ":" + new Help().getInvoke())
			.setPlaceholder("View commands in category")
			.setRequiredRange(1, 1)
			.addOption(musicSelect.getLabel(), ctx.getEventMember().getId() + ":" + musicSelect.getMenuValue())
			.addOption(trackSelect.getLabel(), ctx.getEventMember().getId() + ":" + trackSelect.getMenuValue())
			.addOption(playlistSelect.getLabel(), ctx.getEventMember().getId() + ":" + playlistSelect.getMenuValue())
			.addOption(otherSelect.getLabel(), ctx.getEventMember().getId() + ":" + otherSelect.getMenuValue())
			.build();

		return List.of(
			ActionRow.of(componentList),
			ActionRow.of(selectMenu)
		);
	}

	/**
	 * Returns a list of strings for the home page embeds
	 * @return The list of strings
	 */
	public static List<String> homePageStrings(long guildId) {
		final List<CommandInterface> commandInterfaces = InvokeInterfaceProvider.provideCommands();
		List<String> pageStrings = new ArrayList<>();
		StringBuilder builder = new StringBuilder();

		builder
			.append("Use the select menu to see commands under a category!\n\n");

		int numCommands;
		String commandPlurality;
		for (CategoryInterface category : getCategories()) {
			numCommands = parseCategory(commandInterfaces, category).size();

			builder
				.append(category.getEmoji())
				.append(" ")
				.append(category.getName().substring(0, 1).toUpperCase())
				.append(category.getName().substring(1))
				.append("\n*")
				.append(numCommands)
				.append(" ");

			if (numCommands == 1)
				commandPlurality = "command";
			else
				commandPlurality = "commands";

			builder
				.append(commandPlurality)
				.append("*\n\n");
		}

		pageStrings.add(builder.toString());
		builder = new StringBuilder();

		Stop stop = new Stop();
		builder
			.append("A command that has multiple aliases will be listed with parenthesis.\n***")
			.append(Prefix.getPrefix(guildId))
			.append(stop.getInvoke())
			.append(" (")
			.append(String.join(", ", stop.getAliases()))
			.append(")***\n\n");

		builder
			.append("A command that has parameters will be listed with greater/less than symbols.\n***");

		SkipTo skipTo = new SkipTo();
		builder
			.append(Prefix.getPrefix(guildId))
			.append(skipTo.getInvoke())
			.append(" ")
			.append(skipTo.getUsage())
			.append("***\n\n");

		builder
			.append("Commands can also have optional parameters listed with a paranthesis around " +
				"greater/less than symbols.\n***");

		Play play = new Play();
		builder
			.append(Prefix.getPrefix(guildId))
			.append(play.getInvoke())
			.append(" ")
			.append(play.getUsage())
			.append("***\n\n");

		pageStrings.add(builder.toString());
		return pageStrings;
	}

	/**
	 * Returns a list of categories
	 * @return The list of categories
	 */
	private static List<CategoryInterface> getCategories() {
		return Arrays.asList(
			new MusicCategory(),
			new TrackCategory(),
			new PlaylistCategory(),
			new OtherCategory()
		);
	}

	/**
	 * Returns a list of command interfaces that are in a specified category
	 * @param commands The list of command interfaces
	 * @param category The category to check commands
	 * @return The list of command interfaces in a specified category
	 */
	public static List<CommandInterface> parseCategory(List<CommandInterface> commands,
		CategoryInterface category) {
		List<CommandInterface> parsedCommands = new ArrayList<>();
		for (CommandInterface command : commands) {
			if (command.getCategory() == null)
				continue;

			if (category.getClass() == command.getCategory().getClass())
				parsedCommands.add(command);
		}

		return parsedCommands;
	}

	/**
	 * Returns a list of strings for pagination pages
	 * @param commandList The list of command interfaces
	 * @return The list of strings for pagination
	 */
	public static List<String> buildPages(List<CommandInterface> commandList, long guildId) {
		List<String> stringList = new ArrayList<>();
		StringBuilder builder = new StringBuilder();

		List<String> commandStrings = buildCommandStrings(commandList, guildId);
		int commandsPerPage = 5;
		int numPages = commandStrings.size() / commandsPerPage;
		if ((commandStrings.size() % commandsPerPage) > 0)
			numPages++;

		int currentCommand = 0;
		for (int i = 0; i < numPages; i++) {
			builder.append("```asciidoc\n");

			for (int j = 0; j < commandsPerPage; j++) {
				if (currentCommand >= commandStrings.size())
					break;

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

	/**
	 * Returns a list of strings for pagination based on the command interfaces given
	 * @param commandList The list of command interfaces
	 * @return The list of strings for pagination
	 */
	private static List<String> buildCommandStrings(List<CommandInterface> commandList, long guildId) {
		List<String> commandStrings = new ArrayList<>();

		StringBuilder aliases = new StringBuilder();
		StringBuilder commandInfo = new StringBuilder();
		for (CommandInterface command : commandList) {
			if (!command.getAliases().isEmpty()) {
				aliases
					.append("(")
					.append(String.join(", ", command.getAliases()))
					.append(")");
			}

			commandInfo
				.append(Prefix.getPrefix(guildId))
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
