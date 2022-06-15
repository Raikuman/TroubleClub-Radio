package com.raiku.troubleclub.radio.commands;

import com.raiku.troubleclub.radio.helpers.MessageResources;
import com.raiku.troubleclub.radio.managers.command.CommandContext;
import com.raiku.troubleclub.radio.managers.command.CommandInterface;
import com.raiku.troubleclub.radio.music.PlayerManager;
import net.dv8tion.jda.api.entities.TextChannel;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Handles playing music in a user's voice channel
 *
 * @version 1.0 2022-15-06
 * @since 1.0
 */
public class Play implements CommandInterface {

	@Override
	public void invoke(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel();

		boolean joined = new Join().joinChannel(ctx);

		if (joined)
			ctx.getEvent().getMessage().addReaction("U+1F197").queue();
		else
			ctx.getEvent().getMessage().addReaction("U+1F6AB").queue();

		if (ctx.getArgs().isEmpty()) {
			MessageResources.timedMessage(
				"You must enter a valid link or search for a video",
				ctx.getChannel(),
				10
			);
		}

		String link = String.join(" ", ctx.getArgs());
		if (!isUrl(link))
			link = "ytsearch:" + link;

		PlayerManager.getInstance().loadAndPlay(channel, link, ctx.getEvent().getAuthor());

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "play";
	}

	@Override
	public String getUsage() {
		return "<link>";
	}

	@Override
	public String getDescription() {
		return "Play a song from a link or playlist, or search for a song";
	}

	private boolean isUrl(String url) {
		try {
			new URI(url);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}
}
