package com.raikuman.troubleclub.radio.commands;

import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.DateAndTime;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Handles playing a random song immediately from the queue
 *
 * @version 1.0 2020-25-06
 * @since 1.0
 */
public class Random implements CommandInterface {

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
		AudioTrackInfo audioTrackInfo = musicManager.trackScheduler.random();

		EmbedBuilder builder = new EmbedBuilder()
			.setTitle(audioTrackInfo.title, audioTrackInfo.uri)
			.setColor(RandomColor.getRandomColor())
			.setAuthor("▶️ Playing random song now:", audioTrackInfo.uri,
				ctx.getEventMember().getEffectiveAvatarUrl())
			.addField("Channel", audioTrackInfo.author, true)
			.addField("Song Duration", DateAndTime.formatMilliseconds(audioTrackInfo.length), true)
			.addField("Position in queue", "Now playing", true);

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "random";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Randomly chooses a song from the queue and play immediately, skipping the current playing " +
			"track";
	}
}
