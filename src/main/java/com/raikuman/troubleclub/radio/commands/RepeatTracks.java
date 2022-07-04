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
 * Handles the bot repeating the currently playing song of all audio tracks
 *
 * @version 1.2 2022-03-07
 * @since 1.0
 */
public class RepeatTracks implements CommandInterface {

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

		if ((ctx.getArgs().size() == 0) || ctx.getArgs().size() > 1) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return;
		}

		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

		boolean trackPlaying = false;
		for (Map.Entry<AudioPlayer, TrackScheduler> entry : musicManager.getPlayerMap().entrySet()) {
			if (entry.getKey().getPlayingTrack() != null) {
				trackPlaying = true;
				break;
			}
		}

		if (!trackPlaying) {
			MessageResources.timedMessage(
				"There's currently no song playing",
				channel,
				5
			);
			return;
		}

		String argument = ctx.getArgs().get(0), repeatString, repeatEmoji;
		boolean repeatTrack;
		if (argument.equalsIgnoreCase("on")) {
			repeatString = "Repeating";
			repeatEmoji = "\uD83D\uDD01";
			repeatTrack = true;
		} else if (argument.equalsIgnoreCase("off")) {
			repeatString = "Stopped repeating";
			repeatEmoji = "\uD83D\uDEAB";
			repeatTrack = false;
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
			.setAuthor(repeatEmoji + " " + repeatString + " all audio tracks:");
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
					.append(repeatString)
					.append(" song")
					.append("*: `")
					.append(entry.getKey().getPlayingTrack().getInfo().title)
					.append("`\n");
			} else {
				descriptionBuilder
					.append("*")
					.append(repeatString)
					.append("*: `Nothing`\n");
			}

			descriptionBuilder
				.append("\n");

			entry.getValue().repeat = repeatTrack;
			entry.getValue().repeatQueue = false;

			numTrack++;
		}

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "repeattracks";
	}

	@Override
	public String getUsage() {
		return "<on/off>";
	}

	@Override
	public String getDescription() {
		return "Repeats the current playing song on all audio tracks (invoke command again to stop " +
			"repeating the songs";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"repeatt",
			"rtracks",
			"rt",
			"repeatall",
			"reall"
		);
	}
}
