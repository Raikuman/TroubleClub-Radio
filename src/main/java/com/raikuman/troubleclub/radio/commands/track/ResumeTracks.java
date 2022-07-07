package com.raikuman.troubleclub.radio.commands.track;

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
 * Handles resuming all audio tracks
 *
 * @version 1.1 2022-03-07
 * @since 1.0
 */
public class ResumeTracks implements CommandInterface {

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

		boolean trackPlaying = false, allPlaying = true;
		for (Map.Entry<AudioPlayer, TrackScheduler> entry : musicManager.getPlayerMap().entrySet()) {
			if (entry.getKey().getPlayingTrack() != null) {
				trackPlaying = true;
				continue;
			}

			if (entry.getKey().isPaused())
				allPlaying = false;
		}

		if (!trackPlaying) {
			MessageResources.timedMessage(
				"There's currently no song playing",
				channel,
				5
			);
			return;
		}

		if (allPlaying) {
			MessageResources.timedMessage(
				"The player is already playing",
				channel,
				5
			);
			return;
		}

		EmbedBuilder builder = new EmbedBuilder()
			.setColor(RandomColor.getRandomColor())
			.setAuthor("▶️Resuming all audio tracks:");
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		int numTrack = 1;
		for (Map.Entry<AudioPlayer, TrackScheduler> entry : musicManager.getPlayerMap().entrySet()) {
			descriptionBuilder
				.append("**Audio Track #")
				.append(numTrack)
				.append("**\n");

			if (entry.getKey().getPlayingTrack() != null) {
				descriptionBuilder
					.append("*Resuming song*: `")
					.append(entry.getKey().getPlayingTrack().getInfo().title)
					.append("`\n");
			} else {
				descriptionBuilder
					.append("*Resuming song*: `Nothing`\n");
			}

			descriptionBuilder
				.append("\n");

			if (!entry.getKey().isPaused())
				entry.getKey().setPaused(false);

			numTrack++;
		}

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "resumetracks";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Resumes all audio tracks (if a song is playing)";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"resumet",
			"resumeall",
			"rall"
		);
	}
}
