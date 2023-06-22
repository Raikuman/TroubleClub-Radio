package com.raikuman.troubleclub.radio.commands.track;

import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.radio.category.TrackCategory;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.troubleclub.radio.music.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;
import java.util.Map;

/**
 * Handles the bot looping the queue of all audio tracks
 *
 * @version 1.4 2023-22-06
 * @since 1.1
 */
public class LoopTracks implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel().asTextChannel();
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

		if ((ctx.getArgs().size() == 0) || ctx.getArgs().size() > 1) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return;
		}

		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

		boolean songsInQueue = false;
		for (Map.Entry<AudioPlayer, TrackScheduler> entry : musicManager.getPlayerMap().entrySet()) {
			if (entry.getValue().queue.size() > 0) {
				songsInQueue = true;
				break;
			}
		}

		if (!songsInQueue) {
			MessageResources.timedMessage(
				"There's currently nothing in the queue",
				channel,
				5
			);
			return;
		}

		String argument = ctx.getArgs().get(0), loopString, loopEmoji;
		boolean loopQueue;
		if (argument.equalsIgnoreCase("on")) {
			loopString = "Repeating";
			loopEmoji = "\uD83D\uDD01";
			loopQueue = true;
		} else if (argument.equalsIgnoreCase("off")) {
			loopString = "Stopped repeating";
			loopEmoji = "\uD83D\uDEAB";
			loopQueue = false;
		} else {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return;
		}

		EmbedBuilder builder = new EmbedBuilder()
			.setColor(RandomColor.getRandomColor())
			.setAuthor(loopEmoji + " " + loopString + " all audio tracks:");
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		int numTrack = 1;
		for (Map.Entry<AudioPlayer, TrackScheduler> entry : musicManager.getPlayerMap().entrySet()) {
			descriptionBuilder
				.append("**Audio Track #")
				.append(numTrack)
				.append("**\n");

			if (entry.getKey().getPlayingTrack() != null) {
				descriptionBuilder
					.append("*")
					.append(loopString)
					.append(" queue")
					.append("*: `")
					.append(entry.getValue().queue.size() + 1)
					.append("` songs\n");
			} else {
				descriptionBuilder
					.append("*")
					.append(loopString)
					.append("*: `0` songs\n");
			}

			descriptionBuilder
				.append("\n");

			entry.getValue().repeatQueue = loopQueue;
			entry.getValue().repeat = false;

			numTrack++;
		}

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "looptracks";
	}

	@Override
	public String getUsage() {
		return "<on/off>";
	}

	@Override
	public String getDescription() {
		return "Loops all songs in the queue of all audio tracks by moving a finished playing song to the " +
			"end of the queue (invoke command again to stop looping the queues)";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"loopt",
			"ltracks",
			"lt",
			"loopall",
			"lall"
		);
	}

	@Override
	public CategoryInterface getCategory() {
		return new TrackCategory();
	}
}
