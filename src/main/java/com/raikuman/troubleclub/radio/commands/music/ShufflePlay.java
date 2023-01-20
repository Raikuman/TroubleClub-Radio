package com.raikuman.troubleclub.radio.commands.music;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.troubleclub.radio.category.MusicCategory;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Handles shuffling then playing a playlist
 *
 * @version 1.0 2023-20-01
 * @since 1.2
 */
public class ShufflePlay implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel().asTextChannel();

		new Join().joinChannel(ctx);

		if (ctx.getArgs().size() != 1) {
			MessageResources.timedMessage(
				"You must enter a valid link for a playlist",
				channel,
				10
			);
			return;
		}

		String link = ctx.getArgs().get(0);
		if (!isUrl(link) || !link.contains("https://www.youtube.com/playlist?list=")) {
			MessageResources.timedMessage(
				"You must enter a valid link for a playlist",
				channel,
				10
			);
			return;
		}

		PlayerManager.getInstance().loadAndShuffle(channel, link, ctx.getGuild().getIdLong(), "playlist");

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "shuffleplay";
	}

	@Override
	public String getUsage() {
		return "<youtube playlist url>";
	}

	@Override
	public String getDescription() {
		return "Shuffles and plays a playlist from a link";
	}

	@Override
	public List<String> getAliases() {
		return List.of("sp", "splay", "shufflep");
	}

	@Override
	public CategoryInterface getCategory() {
		return new MusicCategory();
	}

	/**
	 * Check if the string input is a url
	 * @param url Url string
	 * @return If string is url
	 */
	private boolean isUrl(String url) {
		try {
			new URI(url);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}
}
