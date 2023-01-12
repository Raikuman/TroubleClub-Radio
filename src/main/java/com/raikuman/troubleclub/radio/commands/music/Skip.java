package com.raikuman.troubleclub.radio.commands.music;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.helpers.DateAndTime;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.radio.category.MusicCategory;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

/**
 * Handles skipping the current playing track of the music manager
 *
 * @version 1.5 2023-11-01
 * @since 1.1
 */
public class Skip implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel().asTextChannel();
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
		AudioTrackInfo audioTrackInfo = musicManager.getAudioPlayer().getPlayingTrack()
			.getInfo();

		boolean emptyQueue = true;
		String toPlay = "";
		if (musicManager.getTrackScheduler().queue.size() > 0) {
			toPlay = " to play:";
			emptyQueue = false;
		}

		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor("⏭️ Skipped " + audioTrackInfo.title + toPlay)
			.setColor(RandomColor.getRandomColor());

		if (!emptyQueue) {
			AudioTrack nextTrack = musicManager.getTrackScheduler().queue.peek();

			if (nextTrack != null) {
				AudioTrackInfo nextTrackInfo = nextTrack.getInfo();
				builder
					.setTitle(nextTrackInfo.title, nextTrackInfo.uri)
					.addField("Channel", audioTrackInfo.author, true)
					.addField("Song Duration", DateAndTime.formatMilliseconds(audioTrackInfo.length), true)
					.addField("Position in queue", "Now playing", true);
			} else {
				builder
					.setTitle("Unknown song", null)
					.addField("Channel", "Unknown", true)
					.addField("Song Duration", "0", true)
					.addField("Position in queue", "Now playing", true);
			}
		}

		builder.setFooter("Audio track " + musicManager.getCurrentAudioTrack());

		musicManager.getTrackScheduler().nextTrack();

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "skip";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Skips the current playing song";
	}

	@Override
	public List<String> getAliases() {
		return List.of("sk");
	}

	@Override
	public CategoryInterface getCategory() {
		return new MusicCategory();
	}
}
