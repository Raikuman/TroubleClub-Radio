package com.raikuman.troubleclub.radio.commands;

import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.troubleclub.radio.music.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Map;

/**
 * Handles stopping all current playing songs and clears the queue of all audio tracks
 *
 * @version 1.1 2022-30-06
 * @since 1.0
 */
public class StopTracks implements CommandInterface {

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
		int numPlayingSongs = 0, numQueuedSongs = 0;
		for (Map.Entry<AudioPlayer, TrackScheduler> entry : musicManager.getPlayerMap().entrySet()) {
			numQueuedSongs += entry.getValue().queue.size();

			entry.getValue().queue.clear();

			if (entry.getKey().getPlayingTrack() != null)
				numPlayingSongs++;

			entry.getKey().stopTrack();
		}

		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor("\uD83D\uDED1️Stopped " + numPlayingSongs + " playing songs and cleared " +
					numQueuedSongs + " songs from the queue of all audio tracks",
				null,
				ctx.getEventMember().getEffectiveAvatarUrl()
			)
			.setColor(RandomColor.getRandomColor());

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "stoptracks";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Stops playing all playing songs from audio tracks and clears their queues";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"stopt",
			"stracks",
			"st"
		);
	}
}
