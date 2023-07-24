package com.raikuman.troubleclub.radio.commands.other;

import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.radio.category.OtherCategory;
import net.dv8tion.jda.api.EmbedBuilder;

/**
 * Handles sending bot tos embed
 *
 * @version 1.4 2023-30-06
 * @since 1.1
 */
public class ToS implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		EmbedBuilder builder = new EmbedBuilder()
			.setTitle("User Privacy & Security")
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();
		descriptionBuilder
			.append("**Trouble Club Radio**\n")
			.append("This bot will contain information about user ID and message information which consists of " +
				"YouTube urls and is not private or identifying information.\n")
			.append("This information will not be shared to third parties.");

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "tos";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "A description of what information the bot is collecting from the Discord API and what it is" +
			" doing with your data";
	}

	@Override
	public CategoryInterface getCategory() {
		return new OtherCategory();
	}
}
