package com.raikuman.troubleclub.radio.commands.track;

import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.radio.category.TrackCategory;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.troubleclub.radio.music.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;
import java.util.Map;

/**
 * Handles rewinding playing songs on all audio tracks to the beginning of the song
 *
 * @version 1.5 2023-22-06
 * @since 1.1
 */
public class RewindTracks implements CommandInterface {

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

		boolean trackPlaying = false;
		for (Map.Entry<AudioPlayer, TrackScheduler> entry : musicManager.getPlayerMap().entrySet()) {
			if (entry.getKey().getPlayingTrack() != null) {
				trackPlaying = true;
				break;
			}

		}

		if (!trackPlaying) {
			MessageResources.timedMessage(
				"There's currently no song playing",
				channel,
				5
			);
			return;
		}

		EmbedBuilder builder = new EmbedBuilder()
			.setColor(RandomColor.getRandomColor())
			.setAuthor("⏪️ Rewinding tracks:");
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		int numTrack = 1;
		for (Map.Entry<AudioPlayer, TrackScheduler> entry : musicManager.getPlayerMap().entrySet()) {
			descriptionBuilder
				.append("**Audio Track #")
				.append(numTrack)
				.append("**\n");

			if (entry.getKey().getPlayingTrack() != null) {
				descriptionBuilder
					.append("*Rewinding song*: `")
					.append(entry.getKey().getPlayingTrack().getInfo().title)
					.append("`\n");
			} else {
				descriptionBuilder
					.append("*Rewinding song*: `Nothing`\n");
			}

			descriptionBuilder
				.append("\n");

			entry.getValue().rewind();

			numTrack++;
		}

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "rewindtracks";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Rewinds all playing songs on all audio tracks to the beginning";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"rewindt",
			"rewindall"
		);
	}

	@Override
	public CategoryInterface getCategory() {
		return new TrackCategory();
	}
}
