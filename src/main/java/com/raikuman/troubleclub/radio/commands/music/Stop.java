package com.raikuman.troubleclub.radio.commands.music;

import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.troubleclub.radio.music.TrackScheduler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

/**
 * Handles the bot stopping the current playing song and clearing the queue
 *
 * @version 1.6 2022-03-07
 * @since 1.0
 */
public class Stop implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		boolean stopped = stopMusic(ctx);

		if (stopped) {
			final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

			EmbedBuilder builder = new EmbedBuilder()
				.setAuthor("\uD83D\uDED1️Stopped and cleared audio track " + musicManager.getCurrentAudioTrack(),
					null,
					ctx.getEventMember().getEffectiveAvatarUrl()
				)
				.setColor(RandomColor.getRandomColor());

			ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

			ctx.getEvent().getMessage().delete().queue();
		}
	}

	@Override
	public String getInvoke() {
		return "stop";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Stops playing the current song and clears the queue";
	}

	/**
	 * Stops current track and clears queue
	 * @param ctx Provides command context
	 * @return If stopped track/queue
	 */
	public boolean stopMusic(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel();
		final Member self = ctx.getGuild().getSelfMember();
		final GuildVoiceState selfVoiceState = self.getVoiceState();
		if (selfVoiceState == null) {
			MessageResources.connectError(channel, 5);
			return false;
		}

		GuildVoiceState memberVoiceState = ctx.getEventMember().getVoiceState();
		if (memberVoiceState == null) {
			MessageResources.connectError(channel, 5);
			return false;
		}

		if (!memberVoiceState.inAudioChannel()) {
			MessageResources.timedMessage(
				"You must be in a voice channel to use this command",
				channel,
				5
			);
			return false;
		}

		if (selfVoiceState.inAudioChannel() && (selfVoiceState.getChannel() != memberVoiceState.getChannel())) {
			if (selfVoiceState.getChannel() == null) {
				MessageResources.connectError(channel, 5);
				return false;
			}

			MessageResources.timedMessage(
				"You must be in `" + selfVoiceState.getChannel().getName() + "` to use this command",
				channel,
				5
			);
			return false;
		}

		// Clear current track and queue
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
		final TrackScheduler trackScheduler = musicManager.getTrackScheduler();
		trackScheduler.queue.clear();
		trackScheduler.audioPlayer.stopTrack();

		return true;
	}

	@Override
	public List<String> getAliases() {
		return List.of("s");
	}
}
