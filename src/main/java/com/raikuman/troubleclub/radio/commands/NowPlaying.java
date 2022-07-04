package com.raikuman.troubleclub.radio.commands;

import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.DateAndTime;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

/**
 * Handles sending an embed of the current playing track and the state of the audio player
 *
 * @version 1.3 2022-03-07
 * @since 1.0
 */
public class NowPlaying implements CommandInterface {

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

		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
		final AudioPlayer audioPlayer = musicManager.getAudioPlayer();
		AudioTrack audioTrack = audioPlayer.getPlayingTrack();

		if (audioTrack == null) {
			MessageResources.timedMessage(
				"There's currently no song playing",
				channel,
				5
			);
			return;
		}

		String playerState;
		if (audioPlayer.isPaused())
			playerState = "⏸️ Paused ⏸️";
		else
			playerState = "\uD83C\uDFB6 Now Playing \uD83C\uDFB6";

		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor(playerState, audioTrack.getInfo().uri, ctx.getEventMember().getEffectiveAvatarUrl())
			.setTitle(audioTrack.getInfo().title, audioTrack.getInfo().uri)
			.setColor(RandomColor.getRandomColor())
			.addField("Channel", audioTrack.getInfo().author, true)
			.addField(
				"Song Duration",
				DateAndTime.formatMilliseconds(audioTrack.getPosition())
					+ "/" + DateAndTime.formatMilliseconds(audioTrack.getDuration()),
				true
			)
			.setFooter("Audio track " + musicManager.getCurrentAudioTrack());

		channel.sendMessageEmbeds(builder.build()).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "nowplaying";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Shows information about the current playing song";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"np",
			"playing",
			"song"
		);
	}
}
