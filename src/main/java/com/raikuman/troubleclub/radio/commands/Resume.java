package com.raikuman.troubleclub.radio.commands;

import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Handles resuming the music player
 *
 * @version 1.2 2022-24-06
 * @since 1.0
 */
public class Resume implements CommandInterface {

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

		if (!audioPlayer.isPaused()) {
			MessageResources.timedMessage(
				"The player is already playing",
				channel,
				5
			);
			return;
		}

		audioPlayer.setPaused(false);

		ctx.getEvent().getMessage().addReaction("U+25B6").queue();
	}

	@Override
	public String getInvoke() {
		return "resume";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Resumes the current playing song";
	}
}
