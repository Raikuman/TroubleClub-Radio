package com.raiku.troubleclub.radio.commands;

import com.raiku.troubleclub.radio.helpers.MessageResources;
import com.raiku.troubleclub.radio.helpers.RandomColor;
import com.raiku.troubleclub.radio.managers.command.CommandContext;
import com.raiku.troubleclub.radio.managers.command.CommandInterface;
import com.raiku.troubleclub.radio.music.GuildMusicManager;
import com.raiku.troubleclub.radio.music.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Handles the bot repeating the currently playing audio track
 *
 * @version 1.0 2022-16-06
 * @since 1.0
 */
public class Repeat implements CommandInterface {

	@Override
	public void invoke(CommandContext ctx) {
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

		String ifRepeat;
		if (!musicManager.trackScheduler.repeat) {
			musicManager.trackScheduler.repeat = true;
			musicManager.trackScheduler.repeatQueue = false;
			ifRepeat = "\uD83D\uDD01 Now repeating track:";
		} else {
			musicManager.trackScheduler.repeat = false;
			ifRepeat = "\uD83D\uDEAB Stopped repeating track:";
		}

		AudioTrack audioTrack = musicManager.audioPlayer.getPlayingTrack();

		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor(ifRepeat, audioTrack.getInfo().uri, ctx.getEventMember().getAvatarUrl())
			.setTitle(audioTrack.getInfo().title, audioTrack.getInfo().uri)
			.setColor(RandomColor.getRandomColor());

		channel.sendMessageEmbeds(builder.build()).queue();
		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "repeat";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Repeats the currently playing track";
	}
}
