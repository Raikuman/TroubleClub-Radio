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
 * Handles sending an embed of the playing songs and size of queue from all audio tracks
 * player
 *
 * @version 1.1 2022-03-07
 * @since 1.0
 */
public class PlayingTracks implements CommandInterface {

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

		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor("\uD83C\uDFA7 Current state of audio tracks:")
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		int numTrack = 1;
		for (Map.Entry<AudioPlayer, TrackScheduler> entry : musicManager.getPlayerMap().entrySet()) {
			descriptionBuilder
				.append("**Audio Track #")
				.append(numTrack)
				.append("**\n");

			if (entry.getValue().repeat)
				descriptionBuilder.append("\uD83D\uDD01 *Repeating current track*\n");
			else if (entry.getValue().repeatQueue)
				descriptionBuilder.append("\uD83D\uDD03 *Repeating queue*\n");

			if (entry.getKey().getPlayingTrack() != null) {
				descriptionBuilder
					.append("*Playing*: `")
					.append(entry.getKey().getPlayingTrack().getInfo().title)
					.append("`\n");
			} else {
				descriptionBuilder
					.append("*Playing*: `Nothing`\n");
			}

			descriptionBuilder
				.append("*Songs in queue*: `")
				.append(entry.getValue().queue.size())
				.append("`\n\n");

			numTrack++;
		}

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "playingtracks";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Shows current playing songs and size of queue from all audio tracks";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"playingt",
			"ptracks",
			"pt",
			"tracks",
			"playingall"
		);
	}
}
