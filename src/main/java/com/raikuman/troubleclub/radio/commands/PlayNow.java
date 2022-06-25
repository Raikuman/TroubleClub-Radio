package com.raikuman.troubleclub.radio.commands;

import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.DateAndTime;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Handles playing a new song, skipping the current track
 *
 * @version 1.2 2020-24-06
 * @since 1.0
 */
public class PlayNow implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel();
		final Member self = ctx.getGuild().getSelfMember();
		final GuildVoiceState selfVoiceState = self.getVoiceState();
		if (selfVoiceState == null) {
			MessageResources.connectError(channel, 5);
			return;
		}

		GuildVoiceState memberVoiceState = ctx.getEventMember().getVoiceState();
		if (memberVoiceState == null) {
			MessageResources.connectError(channel, 5);
			return;
		}

		if (!memberVoiceState.inAudioChannel()) {
			MessageResources.timedMessage(
				"You must be in a voice channel to use this command",
				channel,
				5
			);
			return;
		}

		if (selfVoiceState.inAudioChannel() && (selfVoiceState.getChannel() != memberVoiceState.getChannel())) {
			if (selfVoiceState.getChannel() == null) {
				MessageResources.connectError(channel, 5);
				return;
			}

			MessageResources.timedMessage(
				"You must be in `" + selfVoiceState.getChannel().getName() + "` to use this command",
				channel,
				5
			);
			return;
		}

		if (ctx.getArgs().isEmpty()) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return;
		}

		if (ctx.getArgs().size() == 1) {
			try {
				int songPos = Integer.parseInt(ctx.getArgs().get(0));

				final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
				musicManager.trackScheduler.moveTrack(songPos);
				musicManager.trackScheduler.nextTrack();

				AudioTrackInfo audioTrackInfo = musicManager.audioPlayer.getPlayingTrack().getInfo();

				EmbedBuilder builder = new EmbedBuilder()
					.setTitle(audioTrackInfo.title, audioTrackInfo.uri)
					.setColor(RandomColor.getRandomColor())
					.setAuthor("▶️ Playing song now:", audioTrackInfo.uri, ctx.getEventMember().getEffectiveAvatarUrl())
					.addField("Channel", audioTrackInfo.author, true)
					.addField("Song Duration", DateAndTime.formatMilliseconds(audioTrackInfo.length), true)
					.addField("Position in queue", "Now playing", true);

				ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

				ctx.getEvent().getMessage().delete().queue();
				return;
			} catch (NumberFormatException e) {
				MessageResources.timedMessage(
					"You must provide a valid argument for this command: `" + getUsage() + "`",
					channel,
					5
				);
				return;
			}
		}

		String link = String.join(" ", ctx.getArgs());
		if (!isUrl(link))
			link = "ytsearch:" + link;

		PlayerManager.getInstance().loadToTop(channel, link, ctx.getEvent().getAuthor(), true);

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "playnow";
	}

	@Override
	public String getUsage() {
		return "<link>, <position #>";
	}

	@Override
	public String getDescription() {
		return "Plays a song or playlist from a link, or search for a song to play immediately, skipping " +
			"the current playing song. If providing a selected song from the queue, it will immediately " +
			"play that song";
	}

	@Override
	public List<String> getAliases() {
		return List.of("pn");
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
