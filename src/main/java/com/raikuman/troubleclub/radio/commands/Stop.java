package com.raikuman.troubleclub.radio.commands;

import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Handles the bot leaving the voice channel of a user
 *
 * @version 1.3 2022-24-06
 * @since 1.0
 */
public class Stop implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		boolean stopped = stopMusic(ctx);

		if (stopped)
			ctx.getEvent().getMessage().addReaction("U+1F6D1").queue();
		else
			ctx.getEvent().getMessage().addReaction("U+1F6AB").queue();
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
		musicManager.trackScheduler.queue.clear();
		musicManager.trackScheduler.audioPlayer.stopTrack();

		return true;
	}
}
