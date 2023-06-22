package com.raikuman.troubleclub.radio.commands.music;

import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.radio.category.MusicCategory;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.helpers.MessageResources;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Handles playing music in a user's voice channel
 *
 * @version 1.15 2023-22-06
 * @since 1.1
 */
public class Play implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel().asTextChannel();

		if (!new Join().joinChannel(ctx)) {
			return;
		}

		if (ctx.getArgs().isEmpty()) {
			final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
			final AudioPlayer audioPlayer = musicManager.getAudioPlayer();
			AudioTrack currentTrack = audioPlayer.getPlayingTrack();

			if (currentTrack == null) {
				MessageResources.timedMessage(
					"You must enter a valid link or search for a video",
					channel,
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
					.setColor(RandomColor.getRandomColor());

				ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

				ctx.getEvent().getMessage().delete().queue();

			} else {
				MessageResources.timedMessage(
					"The player is already playing",
					channel,
					5
				);
			}
			return;
		}

		String link = String.join(" ", ctx.getArgs());
		if (!isUrl(link))
			link = "ytsearch:" + link;

		PlayerManager.getInstance().loadAndPlay(channel, link, ctx.getEvent().getAuthor(), ctx.getGuild().getIdLong());

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "play";
	}

	@Override
	public String getUsage() {
		return "(<link>)";
	}

	@Override
	public String getDescription() {
		return "Plays a song or playlist from a link, or search for a song to play. Also can resume a " +
			"paused audio track provided no link";
	}

	@Override
	public List<String> getAliases() {
		return List.of("p");
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
