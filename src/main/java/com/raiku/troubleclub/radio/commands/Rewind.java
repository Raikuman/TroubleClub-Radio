package com.raiku.troubleclub.radio.commands;

import com.raiku.troubleclub.radio.music.GuildMusicManager;
import com.raiku.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Handles rewinding the current playing track to the beginning of the track
 *
 * @version 1.0 2020-23-06
 * @since 1.0
 */
public class Rewind implements CommandInterface {

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

		if (musicManager.audioPlayer.getPlayingTrack() == null) {
			MessageResources.timedMessage(
				"There's currently no track playing",
				channel,
				5
			);
			return;
		}

		musicManager.trackScheduler.rewind();

		ctx.getEvent().getMessage().addReaction("⏪").queue();
	}

	@Override
	public String getInvoke() {
		return "rewind";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Rewinds the current track to the beginning";
	}
}
