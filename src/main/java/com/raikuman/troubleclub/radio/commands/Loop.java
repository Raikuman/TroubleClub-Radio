package com.raikuman.troubleclub.radio.commands;

import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.radio.music.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

/**
 * Handles the bot looping the current audio player queue
 *
 * @version 1.4 2022-29-06
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
		final AudioPlayer audioPlayer = musicManager.getAudioPlayer();
		final TrackScheduler trackScheduler = musicManager.getTrackScheduler();
		if (audioPlayer.getPlayingTrack() == null && trackScheduler.queue.isEmpty()) {
			MessageResources.timedMessage(
				"There's currently nothing in the queue",
				channel,
				5
			);
			return;
		}

		String ifRepeat;
		if (!trackScheduler.repeatQueue) {
			trackScheduler.repeatQueue = true;
			trackScheduler.repeat = false;
			ifRepeat = "\uD83D\uDD01 Now repeating queue:";
		} else {
			trackScheduler.repeatQueue = false;
			ifRepeat = "\uD83D\uDEAB Stopped repeating queue:";
		}

		AudioTrack audioTrack = audioPlayer.getPlayingTrack();
		int totalSongs = trackScheduler.queue.size() + 1;

		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor(ifRepeat, null, ctx.getEventMember().getEffectiveAvatarUrl())
			.setTitle(audioTrack.getInfo().title, audioTrack.getInfo().uri)
			.setColor(RandomColor.getRandomColor())
			.setFooter("Audio track " + musicManager.getCurrentAudioTrack());
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
		return "Loops all songs in the queue by moving a finished playing track to the end of the queue " +
			"(invoke command again to stop looping the queue)";
	}

	@Override
	public List<String> getAliases() {
		return List.of("repeatqueue");
	}
}
