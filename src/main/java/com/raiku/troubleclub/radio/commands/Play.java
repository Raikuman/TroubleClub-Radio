package com.raiku.troubleclub.radio.commands;

import com.raiku.botutilities.commands.manager.CommandContext;
import com.raiku.botutilities.commands.manager.CommandInterface;
import com.raiku.botutilities.helpers.MessageResources;
import com.raiku.troubleclub.radio.music.GuildMusicManager;
import com.raiku.troubleclub.radio.music.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Handles playing music in a user's voice channel
 *
 * @version 1.1 2022-18-06
 * @since 1.0
 */
public class Play implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel();

		boolean joined = new Join().joinChannel(ctx);

		if (joined)
			ctx.getEvent().getMessage().addReaction("U+1F197").queue();
		else
			ctx.getEvent().getMessage().addReaction("U+1F6AB").queue();

		if (ctx.getArgs().isEmpty()) {
			final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
			AudioTrack currentTrack = musicManager.trackScheduler.audioPlayer.getPlayingTrack();

			if (currentTrack == null) {
				MessageResources.timedMessage(
					"You must enter a valid link or search for a video",
					ctx.getChannel(),
					10
				);
				return;
			}

			if (musicManager.audioPlayer.isPaused()) {
				musicManager.audioPlayer.setPaused(false);
				ctx.getEvent().getMessage().addReaction("U+25B6").queue();
				return;
			}
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
