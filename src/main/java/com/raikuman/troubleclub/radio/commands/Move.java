package com.raikuman.troubleclub.radio.commands;

import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

/**
 * Handles moving a selected song to the top of the queue, or moving the song to a certain position of the
 * queue
 *
 * @version 1.1 2020-23-06
 * @since 1.0
 */
public class Move implements CommandInterface {

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

		if ((ctx.getArgs().size() == 0) || ctx.getArgs().size() > 2) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return;
		}

		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

		if (ctx.getArgs().size() == 1) {
			try {
				int songNum = Integer.parseInt(ctx.getArgs().get(0));

				if (songNum > musicManager.trackScheduler.queue.size()) {
					MessageResources.timedMessage(
						"You must select a valid track number from the queue",
						channel,
						5
					);
					return;
				}

				AudioTrackInfo audioTrackInfo = musicManager.trackScheduler.moveTrack(songNum);
				EmbedBuilder builder = new EmbedBuilder()
					.setTitle(audioTrackInfo.title, audioTrackInfo.uri)
					.setColor(RandomColor.getRandomColor())
					.setAuthor(
						"Moved song to position 1 in queue",
						audioTrackInfo.uri,
						ctx.getEventMember().getEffectiveAvatarUrl()
					);

				ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
			} catch (NumberFormatException e) {
				MessageResources.timedMessage(
					"You must provide a valid argument for this command: `" + getUsage() + "`",
					channel,
					5
				);
				return;
			}
		}

		if (ctx.getArgs().size() == 2) {
			try {
				int songNum = Integer.parseInt(ctx.getArgs().get(0));

				if (songNum > musicManager.trackScheduler.queue.size()) {
					MessageResources.timedMessage(
						"You must select a valid track number from the queue",
						channel,
						5
					);
					return;
				}

				int posNum = Integer.parseInt(ctx.getArgs().get(1));

				if (posNum > musicManager.trackScheduler.queue.size()) {
					MessageResources.timedMessage(
						"You must select a valid position number to add to the queue",
						channel,
						5
					);
					return;
				}

				AudioTrackInfo audioTrackInfo = musicManager.trackScheduler.moveTrack(songNum, posNum);
				EmbedBuilder builder = new EmbedBuilder()
					.setTitle(audioTrackInfo.title, audioTrackInfo.uri)
					.setColor(RandomColor.getRandomColor())
					.setAuthor(
						"\uD83D\uDCE6 Moved song to position " + posNum + " in queue",
						audioTrackInfo.uri,
						ctx.getEventMember().getEffectiveAvatarUrl()
					);

				ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
			} catch (NumberFormatException e) {
				MessageResources.timedMessage(
					"You must provide a valid argument for this command: `" + getUsage() + "`",
					channel,
					5
				);
			}
		}

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "move";
	}

	@Override
	public String getUsage() {
		return "<song #> (<position #>)";
	}

	@Override
	public String getDescription() {
		return "Moves a selected song to the top of the queue or to a position in the queue";
	}

	@Override
	public List<String> getAliases() {
		return List.of("m");
	}
}
