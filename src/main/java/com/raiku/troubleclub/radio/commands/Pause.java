package com.raiku.troubleclub.radio.commands;

import com.raiku.troubleclub.radio.helpers.MessageResources;
import com.raiku.troubleclub.radio.managers.command.CommandContext;
import com.raiku.troubleclub.radio.managers.command.CommandInterface;
import com.raiku.troubleclub.radio.music.PlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Handles pausing the music player
 *
 * @version 1.0 2022-15-06
 * @since 1.0
 */
public class Pause implements CommandInterface {

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

		final AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer;

		if (audioPlayer.getPlayingTrack() == null) {
			MessageResources.timedMessage(
				"There's currently no track playing",
				channel,
				5
			);
			return;
		}

		if (audioPlayer.isPaused()) {
			MessageResources.timedMessage(
				"The player is already paused",
				channel,
				5
			);
			return;
		}

		audioPlayer.setPaused(true);

		ctx.getEvent().getMessage().addReaction("U+23F8").queue();
	}

	@Override
	public String getInvoke() {
		return "pause";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Pauses the current track";
	}
}
