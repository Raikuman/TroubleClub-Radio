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
 * Handles removing duplicate songs from the queue of all audio tracks
 *
 * @version 1.0 2022-03-07
 * @since 1.0
 */
public class PruneTracks implements CommandInterface {

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
			.setAuthor("✂️ Pruning all audio tracks:")
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		int numTrack = 1, totalPruned = 0, currentPruned;
		for (Map.Entry<AudioPlayer, TrackScheduler> entry : musicManager.getPlayerMap().entrySet()) {
			descriptionBuilder
				.append("**Audio Track #")
				.append(numTrack)
				.append("**\n");

			currentPruned = entry.getValue().pruneTracks();
			totalPruned += currentPruned;

			descriptionBuilder
				.append("*Pruned*: `")
				.append(currentPruned)
				.append("` songs\n\n");

			numTrack++;
		}

		descriptionBuilder
			.append("**Pruned** `")
			.append(totalPruned)
			.append("` **songs in total**");

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "pruneall";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Removes duplicate songs from the queue of all audio tracks";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"snipall",
			"cutall"
		);
	}
}
