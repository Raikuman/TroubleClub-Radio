package com.raikuman.troubleclub.radio.commands.other;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.radio.category.OtherCategory;
import net.dv8tion.jda.api.EmbedBuilder;

/**
 * Handles sending bot tos embed
 *
 * @version 1.2 2022-09-07
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
			.append("In order to meet Discord's terms of service as listed in the following: ")
			.append("https://discord.com/developers/docs/policies-and-agreements/terms-of-service")
			.append("\n\n")
			.append("**Section 2a. Implement Good Privacy Practices**\n")
			.append("You will comply with all applicable privacy laws and regulations including those ")
			.append("applying to personally identifiable information (\"PII\"). You will provide and adhere ")
			.append("to a privacy policy for your application that uses the API (your “API Client”) that ")
			.append("clearly and accurately describes to users of your API Client what user information ")
			.append("you collect and how you use and share such information with Discord and third parties.")
			.append("\n\n")
			.append("**Section 2b. Implement Good Security**\n")
			.append("You will use commercially reasonable efforts to protect data collected by your API ")
			.append("Client, including PII, from unauthorized access or use. These efforts will include, but ")
			.append("are not limited to, encryption of this data at rest. You will promptly report to your ")
			.append("users any unauthorized access or use of such information to the extent required by ")
			.append("applicable law.")
			.append("\n\n")
			.append("**Trouble Club Radio**\n")
			.append("This bot will contain information about user ID and message information ")
			.append("(will be encrypted on a local database) for playlist functionality ")
			.append("by saving links when invoking a command. ")
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
