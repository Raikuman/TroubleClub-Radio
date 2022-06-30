package com.raikuman.troubleclub.radio.commands;

import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Handles playing music in a user's voice channel
 *
 * @version 1.5 2022-29-06
 * @since 1.0
 */
public class Play implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel();

		new Join().joinChannel(ctx);

		if (ctx.getArgs().isEmpty()) {
			final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
			final AudioPlayer audioPlayer = musicManager.getAudioPlayer();
			AudioTrack currentTrack = audioPlayer.getPlayingTrack();

			if (currentTrack == null) {
				MessageResources.timedMessage(
					"You must enter a valid link or search for a video",
					ctx.getChannel(),
					10
				);
				return;
			}

			if (audioPlayer.isPaused()) {
				audioPlayer.setPaused(false);

				EmbedBuilder builder = new EmbedBuilder()
					.setAuthor("▶️Resuming audio track " + musicManager.getCurrentAudioTrack(),
						null,
						ctx.getEventMember().getEffectiveAvatarUrl()
					)
					.setFooter("Audio track " + musicManager.getCurrentAudioTrack());

				ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

				ctx.getEvent().getMessage().delete().queue();

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
		return "Plays a song or playlist from a link, or search for a song to play";
	}

	@Override
	public List<String> getAliases() {
		return List.of("p");
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
