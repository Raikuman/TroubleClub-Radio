package com.raikuman.troubleclub.radio.commands.other;

import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.RandomColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

/**
 * Handles sending an embed with informational links about the bot
 *
 * @version 1.0 2022-29-06
 * @since 1.0
 */
public class Changelog implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor("Changelog", null, ctx.getEvent().getAuthor().getEffectiveAvatarUrl())
			.setColor(RandomColor.getRandomColor())
			.setDescription("The buttons below will bring you to the Trello board or the GitHub repo for this " +
				"project!");

		ctx.getChannel().sendMessageEmbeds(builder.build())
			.setActionRow(
				Button.link("https://github.com/Raikuman/TroubleClub-Radio", "GitHub")
					.withEmoji(Emoji.fromMarkdown("<:github:849286315580719104>")),
				Button.link("https://trello.com/b/7MGa3xQO/trouble-club-radio", "Trello")
					.withEmoji(Emoji.fromMarkdown("📃"))
			).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "changelog";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Get useful links for more information on this bot";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"log",
			"cl"
		);
	}
}
