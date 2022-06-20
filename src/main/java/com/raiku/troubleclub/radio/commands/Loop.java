package com.raiku.troubleclub.radio.commands;

import com.raiku.troubleclub.radio.music.GuildMusicManager;
import com.raiku.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Handles the bot looping the current audio player queue
 *
 * @version 1.2 2022-20-06
 * @since 1.0
 */
public class Loop implements CommandInterface {

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
		if (musicManager.audioPlayer.getPlayingTrack() == null && musicManager.trackScheduler.queue.isEmpty()) {
			MessageResources.timedMessage(
				"There's currently nothing in the queue",
				channel,
				5
			);
			return;
		}

		String ifRepeat;
		if (!musicManager.trackScheduler.repeatQueue) {
			musicManager.trackScheduler.repeatQueue = true;
			musicManager.trackScheduler.repeat = false;
			ifRepeat = "\uD83D\uDD01 Now repeating queue:";
		} else {
			musicManager.trackScheduler.repeatQueue = false;
			ifRepeat = "\uD83D\uDEAB Stopped repeating queue:";
		}

		AudioTrack audioTrack = musicManager.audioPlayer.getPlayingTrack();
		int totalSongs = musicManager.trackScheduler.queue.size() + 1;

		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor(ifRepeat, null, ctx.getEventMember().getEffectiveAvatarUrl())
			.setTitle(audioTrack.getInfo().title, audioTrack.getInfo().uri)
			.setColor(RandomColor.getRandomColor());
		builder
			.addField("Songs in queue", "`" + totalSongs + "`songs", true);

		channel.sendMessageEmbeds(builder.build()).queue();
		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "loop";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Loops the queue";
	}
}
