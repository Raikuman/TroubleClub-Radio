package com.raikuman.troubleclub.radio.commands.help;

import com.raikuman.botutilities.configs.Prefix;
import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.context.EventContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.botutilities.invokes.interfaces.SlashInterface;
import com.raikuman.botutilities.invokes.manager.InvokeProvider;
import com.raikuman.troubleclub.radio.category.MusicCategory;
import com.raikuman.troubleclub.radio.category.OtherCategory;
import com.raikuman.troubleclub.radio.category.PlaylistCategory;
import com.raikuman.troubleclub.radio.category.TrackCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A utility class to help construct pages for the help slash command
 *
 * @version 1.0 2023-22-06
 * @since 1.3
 */
public class HelpUtilities {

	/**
	 * Build the page strings for the main help slash
	 * @param ctx The EventContext to retrieve data from
	 * @param invokeProvider The InvokeProvider to count invokes for
	 * @return The list of page strings
	 */
    public static List<String> homePageStrings(EventContext ctx, InvokeProvider invokeProvider) {
        List<String> pageStrings = new ArrayList<>();
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder
			.append("Use the select menu to see commands under a category!\n\n");

		// Set strings for categories
		int numCommands;
        List<CategoryInterface> categories = Arrays.asList(
            new MusicCategory(), new TrackCategory(), new PlaylistCategory(), new OtherCategory()
        );


        for (CategoryInterface category : categories) {
			numCommands = numInvokesInCategory(category, invokeProvider);

			// Handle number of commands in category
			stringBuilder
				.append(category.getEmoji())
				.append(" **")
				.append(category.getName().substring(0, 1).toUpperCase())
				.append(category.getName().substring(1))
				.append("**\n*")
				.append(numCommands)
				.append(" command");

			// Handle plurality
			if (numCommands > 1)
				stringBuilder.append("s");

			stringBuilder
				.append("*\n\n");
		}

		// Create page for number of commands
		pageStrings.add(stringBuilder.toString());
		stringBuilder = new StringBuilder();

		// Set strings for aliases
		stringBuilder
			.append("A command that has multiple aliases will be listed with parenthesis.\n***")
			.append(Prefix.getPrefix(ctx.getGuild().getIdLong()))
			.append("command (c, com, comm)***\n\n");

		// Set strings for parameters
		stringBuilder
			.append("A command that requires parameters will be listed with greater/less than symbols.\n***")
			.append(Prefix.getPrefix(ctx.getGuild().getIdLong()))
			.append("command <# of things>***\n\n");

		// Set strings for optional parameters
		stringBuilder
			.append("Commands can also have optional parameters listed with a parenthesis around " +
				"greater/less than symbols.\n***")
			.append(Prefix.getPrefix(ctx.getGuild().getIdLong()))
			.append("command (<user>)***\n\n");

		// Create page for command examples
		pageStrings.add(stringBuilder.toString());
		return pageStrings;
    }

	/**
	 * Counts the number of invokes in a category
	 * @param categoryInterface The category to count invokes in
	 * @param invokeProvider The InvokeProvider to count invokes in
	 * @return The number of invokes in a category
	 */
    private static int numInvokesInCategory(CategoryInterface categoryInterface, InvokeProvider invokeProvider) {
        int count = 0;
        for (CommandInterface command : invokeProvider.commands) {
            if (command.getCategory().getClass() == categoryInterface.getClass()) count++;
        }

        for (SlashInterface slash : invokeProvider.slashes) {
			if (slash.getInvoke().equals(new Help(null).getInvoke())) continue;

            if (slash.getCategory().getClass() == categoryInterface.getClass()) count++;
        }

        return count;
    }

	/**
	 * Build the page strings for the category
	 * @param ctx The EventContext to retrieve data from
	 * @param invokeProvider The InvokeProvider to generate pages with
	 * @return The list of page strings
	 */
	public static List<String> categoryPageStrings(EventContext ctx, InvokeProvider invokeProvider, CategoryInterface category) {
		List<String> helpPages = new ArrayList<>();
		StringBuilder helpBuilder = new StringBuilder();
		helpBuilder.append("```asciidoc\n");

		int characterCount = 0;
		for (String invoke : categoryInvokes(ctx, invokeProvider, category)) {
			helpBuilder
				.append(invoke)
				.append("\n\n");

			characterCount += invoke.length();

			// Construct next page when current page has reached a certain amount of characters
			if (characterCount > 550) {
				helpBuilder.append("\n```");
				helpPages.add(helpBuilder.toString());

				helpBuilder = new StringBuilder();
				helpBuilder.append("```asciidoc\n");

				characterCount = 0;
			}
		}

		helpBuilder.append("\n```");
		helpPages.add(helpBuilder.toString());

		return helpPages;
	}

	/**
	 * Creates a list of invoke strings to prepare making pages from
	 * @param ctx The EventContext to retrieve data from
	 * @param invokeProvider The InvokeProvider to generate invoke strings with
	 * @param category The category to get invokes from
	 * @return The list of invoke strings
	 */
	private static List<String> categoryInvokes(EventContext ctx, InvokeProvider invokeProvider, CategoryInterface category) {
		List<String> invokeStrings = new ArrayList<>();
		StringBuilder invokeBuilder = new StringBuilder();
		StringBuilder invokeAliases = new StringBuilder();

		// Build slash invokes
		for (SlashInterface slash : invokeProvider.slashes) {
			if (slash.getInvoke().equals(new Help(null).getInvoke())) continue;

			if (slash.getCategory().getClass() != category.getClass()) continue;

			invokeBuilder
				.append("/")
				.append(slash.getInvoke())
				.append(" :: ")
				.append(slash.getDescription());

			invokeStrings.add(invokeBuilder.toString());
			invokeBuilder = new StringBuilder();
		}

		// Build command invokes
		for (CommandInterface command : invokeProvider.commands) {
			if (command.getCategory().getClass() != category.getClass()) continue;

			if (!command.getAliases().isEmpty()) {
				invokeAliases
					.append("(")
					.append(String.join(", ", command.getAliases()))
					.append(")");
			}

			invokeBuilder
				.append(Prefix.getPrefix(ctx.getGuild().getIdLong()))
				.append(command.getInvoke())
				.append(" ");

			if (invokeAliases.length() > 0)
				invokeBuilder
					.append(invokeAliases)
					.append(" ");

			if (!command.getUsage().isEmpty())
				invokeBuilder
					.append(command.getUsage())
					.append(" ");

			invokeBuilder
				.append(":: ")
				.append(command.getDescription());

			invokeStrings.add(invokeBuilder.toString());
			invokeAliases = new StringBuilder();
			invokeBuilder = new StringBuilder();
		}

		return invokeStrings;
	}
}
